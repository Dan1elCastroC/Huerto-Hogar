param([ValidateRange(1024, 65535)][int]$Puerto = 65534)

$ErrorActionPreference = 'Stop'
if (Get-NetTCPConnection -LocalPort $Puerto -State Listen -ErrorAction SilentlyContinue) {
    throw "El puerto $Puerto está ocupado. Repite con -Puerto y otro puerto libre."
}

$apiAnterior = [Environment]::GetEnvironmentVariable('VITE_API_URL', 'Process')
try {
    $env:VITE_API_URL = "http://127.0.0.1:$Puerto"
    Write-Host "DEMOSTRACIÓN: se esperan 2 pruebas fallidas porque $env:VITE_API_URL no tiene backend."
    Push-Location $PSScriptRoot
    try {
        & npm.cmd run test:integration
        $resultado = $LASTEXITCODE
    } finally {
        Pop-Location
    }
    if ($resultado -eq 0) {
        throw 'La demostración no produjo el fallo de conexión esperado.'
    }
    Write-Host 'Fallo provocado. Se restaura VITE_API_URL; el backend y sus datos no se modificaron.'
} finally {
    [Environment]::SetEnvironmentVariable('VITE_API_URL', $apiAnterior, 'Process')
}
exit $resultado
