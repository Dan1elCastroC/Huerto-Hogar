[CmdletBinding()]
param(
    [string]$XamppPath = 'C:\xampp',
    [string]$JavaHome = 'C:\Program Files\Java\jdk-17',
    [string]$MavenPath,
    [string]$NpmPath
)

$ErrorActionPreference = 'Stop'
$repo = $PSScriptRoot
$logs = Join-Path ([IO.Path]::GetTempPath()) ('HuertoHogar-demo\' + (Get-Date -Format 'yyyyMMdd-HHmmss-fff'))
New-Item -ItemType Directory -Path $logs -Force | Out-Null
$shell = (Get-Process -Id $PID).Path

function Require-File([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { throw "No existe: $Path" }
    return (Resolve-Path -LiteralPath $Path).Path
}
function Quote-PS([string]$Value) { return "'" + $Value.Replace("'", "''") + "'" }
function Port-Owner([int]$Port) {
    $client = [Net.Sockets.TcpClient]::new()
    try {
        $connection = $client.ConnectAsync('127.0.0.1', $Port)
        if (-not $connection.Wait(700) -or -not $client.Connected) { return $null }
    } catch { return $null } finally { $client.Dispose() }
    $owner = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($owner) { return $owner }
    # Some Windows providers omit a listening socket although its TCP connection succeeds.
    return [pscustomobject]@{ OwningProcess = $null }
}
function Wait-Port([int]$Port, $Process, [string]$Name) {
    for ($i = 0; $i -lt 90; $i++) {
        if (Port-Owner $Port) { return }
        $Process.Refresh()
        if ($Process.HasExited) { throw "$Name se cerró al iniciar. Revisa $logs" }
        Start-Sleep -Seconds 1
    }
    throw "$Name no abrió el puerto $Port en 90 segundos. Revisa $logs"
}
function Start-XamppService([string]$Name, [string]$Executable, [string[]]$Arguments, [int]$Port, [string]$ProcessName) {
    $owner = Port-Owner $Port
    if ($owner) {
        $existing = if ($owner.OwningProcess) { Get-Process -Id $owner.OwningProcess -ErrorAction SilentlyContinue }
        if (-not $existing) { $existing = Get-Process -Name $ProcessName -ErrorAction SilentlyContinue | Where-Object Path -eq $Executable | Select-Object -First 1 }
        if (-not $existing) { throw "El puerto $Port responde pero no se identifica como $Name de XAMPP." }
        if ($existing.ProcessName -ne $ProcessName) { throw "Puerto $Port ocupado por $($existing.ProcessName), PID $($existing.Id)." }
        if ($existing.Path -and $existing.Path -ne $Executable) { throw "El puerto $Port pertenece a otra instalación: $($existing.Path)" }
        Write-Host "$Name ya está activo (PID $($existing.Id))."
        return
    }
    Write-Host "Iniciando $Name..."
    $process = Start-Process -FilePath $Executable -ArgumentList $Arguments -WorkingDirectory $XamppPath -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $logs "$Name.out.log") -RedirectStandardError (Join-Path $logs "$Name.err.log")
    Wait-Port $Port $process $Name
}
function New-Runner([string]$Name, [string]$Directory, [string]$Executable, [string[]]$Arguments) {
    $file = Join-Path $logs "$Name.ps1"
    $lines = @(
        '$ErrorActionPreference = ''Stop''',
        ('$env:JAVA_HOME = ' + (Quote-PS $JavaHome)),
        ('$env:Path = ' + (Quote-PS ((Join-Path $JavaHome 'bin') + ';' + (Split-Path $NpmPath -Parent) + ';')) + ' + $env:Path'),
        '$env:SERVER_PORT = ''8087''',
        '$env:VITE_API_URL = ''http://localhost:8087''',
        ('$env:LOGGING_FILE_NAME = ' + (Quote-PS (Join-Path $logs 'backend-app.log'))),
        ('Set-Location -LiteralPath ' + (Quote-PS $Directory)),
        ('& ' + (Quote-PS $Executable) + ' ' + (($Arguments | ForEach-Object { Quote-PS $_ }) -join ' ')),
        'exit $LASTEXITCODE'
    )
    [IO.File]::WriteAllLines($file, $lines, [Text.UTF8Encoding]::new($true))
    return $file
}
function Start-Runner([string]$Name, [string]$File, [switch]$Wait) {
    $options = @{
        FilePath = $shell
        ArgumentList = @('-NoProfile', '-ExecutionPolicy', 'Bypass', '-File', ('"' + $File + '"'))
        WindowStyle = 'Hidden'
        PassThru = $true
        RedirectStandardOutput = (Join-Path $logs "$Name.out.log")
        RedirectStandardError = (Join-Path $logs "$Name.err.log")
    }
    if ($Wait) { $options.Wait = $true }
    return Start-Process @options
}
function Wait-Web([string]$Url, $Process) {
    for ($i = 0; $i -lt 120; $i++) {
        try { return Invoke-WebRequest -UseBasicParsing -Uri $Url -TimeoutSec 3 } catch {}
        if ($Process) {
            $Process.Refresh()
            if ($Process.HasExited) { throw "El proceso se cerró antes de responder en $Url. Revisa $logs" }
        }
        Start-Sleep -Seconds 1
    }
    throw "No respondió $Url. Revisa $logs"
}
function Run-Tests([string]$Name, [string]$Directory, [string]$Executable, [string[]]$Arguments) {
    Write-Host "Ejecutando $Name..."
    $file = New-Runner $Name $Directory $Executable $Arguments
    $process = Start-Runner $Name $file -Wait
    Write-Host "${Name}: código de salida $($process.ExitCode)"
    Get-Content -LiteralPath (Join-Path $logs "$Name.out.log") -Tail 7 | ForEach-Object { Write-Host $_ }
    if ($process.ExitCode -ne 0) { Write-Host "Detalle: $(Join-Path $logs "$Name.err.log")" }
    return [int]$process.ExitCode
}

try {
    $java = Require-File (Join-Path $JavaHome 'bin\java.exe')
    $ErrorActionPreference = 'Continue'
    try { $javaVersion = (& $java -version 2>&1 | Out-String) } finally { $ErrorActionPreference = 'Stop' }
    if ($javaVersion -notmatch 'version "17[\."]') { throw 'Se requiere Java 17. Indica -JavaHome con el JDK 17 instalado.' }
    if (-not $MavenPath) {
        $found = Get-Command mvn.cmd -ErrorAction SilentlyContinue
        if ($found) { $MavenPath = $found.Source }
        else {
            $cache = Join-Path $env:USERPROFILE '.m2\wrapper\dists\apache-maven-3.9.12'
            $MavenPath = Get-ChildItem -LiteralPath $cache -Filter mvn.cmd -Recurse -File -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName
        }
    }
    if (-not $MavenPath) { throw 'Maven no encontrado. Configura PATH o indica -MavenPath con mvn.cmd.' }
    $MavenPath = Require-File $MavenPath
    if (-not $NpmPath) {
        $found = Get-Command npm.cmd -ErrorAction SilentlyContinue
        if ($found) { $NpmPath = $found.Source }
        elseif (Test-Path -LiteralPath 'D:\Program Files\nodejs\npm.cmd') { $NpmPath = 'D:\Program Files\nodejs\npm.cmd' }
    }
    if (-not $NpmPath) { throw 'npm no encontrado. Configura PATH o indica -NpmPath con npm.cmd.' }
    $NpmPath = Require-File $NpmPath
    if (-not (Test-Path -LiteralPath (Join-Path $repo 'Frontend\node_modules\vite\bin\vite.js'))) { throw 'Faltan dependencias del frontend. Ejecuta npm ci dentro de Frontend antes de iniciar la demo.' }
    $mysql = Require-File (Join-Path $XamppPath 'mysql\bin\mysqld.exe')
    $apache = Require-File (Join-Path $XamppPath 'apache\bin\httpd.exe')
    Write-Host "Registros de esta ejecución: $logs"
    Start-XamppService 'MySQL' $mysql @(('--defaults-file="' + (Join-Path $XamppPath 'mysql\bin\my.ini') + '"'), '--standalone') 3306 'mysqld'
    Start-XamppService 'Apache' $apache @('-f', ('"' + (Join-Path $XamppPath 'apache\conf\httpd.conf') + '"')) 80 'httpd'
    $apacheResponse = Wait-Web 'http://127.0.0.1/' $null
    if (($apacheResponse.Headers['Server'] | Out-String) -notmatch 'Apache') { throw 'El puerto 80 responde pero no corresponde a Apache.' }

    $backend = $null
    if (-not (Port-Owner 8087)) {
        Write-Host 'Iniciando backend en 8087...'
        $file = New-Runner 'backend' (Join-Path $repo 'Backend') $MavenPath @('spring-boot:run')
        $backend = Start-Runner 'backend' $file
    }
    $response = Wait-Web 'http://localhost:8087/api/v1/entities/productos' $backend
    $products = $response.Content | ConvertFrom-Json
    if (-not ($products | Where-Object codigo -eq 'KIT-001')) { throw 'La API no entrega KIT-001. Comprueba datos iniciales y el servicio que usa el puerto 8087.' }
    $frontend = $null
    if (-not (Port-Owner 5173)) {
        Write-Host 'Iniciando frontend en 5173...'
        $file = New-Runner 'frontend' (Join-Path $repo 'Frontend') $NpmPath @('run','dev','--','--host','127.0.0.1','--port','5173','--strictPort')
        $frontend = Start-Runner 'frontend' $file
    }
    $response = Wait-Web 'http://127.0.0.1:5173' $frontend
    $title = [regex]::Match((Get-Content -LiteralPath (Join-Path $repo 'Frontend\index.html') -Raw), '<title>(.*?)</title>').Groups[1].Value
    if ($response.Content -notmatch [regex]::Escape($title)) { throw 'El puerto 5173 corresponde a otra aplicación.' }
    $results = @(
        (Run-Tests 'backend-tests' (Join-Path $repo 'Backend') $MavenPath @('test')),
        (Run-Tests 'frontend-tests' (Join-Path $repo 'Frontend') $NpmPath @('test')),
        (Run-Tests 'integracion-tests' (Join-Path $repo 'Frontend') $NpmPath @('run','test:integration'))
    )
    $services = @(foreach ($entry in @(@('Apache',80),@('MySQL',3306),@('Backend',8087),@('Frontend',5173))) {
        $owner = Port-Owner $entry[1]
        if (-not $owner) { throw "$($entry[0]) dejó de escuchar en el puerto $($entry[1])." }
        [pscustomobject]@{ servicio=$entry[0]; puerto=$entry[1]; pid=$owner.OwningProcess }
    })
    $services | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $logs 'servicios.json') -Encoding UTF8
    $services | ForEach-Object { $pidLabel = if ($_.pid) { $_.pid } else { 'no informado por Windows' }; Write-Host "$($_.servicio): puerto $($_.puerto), PID $pidLabel" }
    Write-Host "Tienda: http://localhost:5173 | API: http://localhost:8087 | Logs: $logs"
    Write-Host 'Los servicios quedan activos para la presentación.'
    if ($results | Where-Object { $_ -ne 0 }) { exit 1 }
    Write-Host 'Todas las pruebas finalizaron correctamente.'
    exit 0
} catch {
    Write-Error "$($_.Exception.Message) Los servicios iniciados se conservan; registros: $logs" -ErrorAction Continue
    exit 1
}
