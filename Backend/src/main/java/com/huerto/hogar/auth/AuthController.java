package com.huerto.hogar.auth;

import com.huerto.hogar.exception.ReglaDeNegocioException;
import com.huerto.hogar.security.jwt.JwtUtil;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación")
public class AuthController {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            UsuarioEntity u = usuarioRepo.findByCorreo(req.getCorreo())
                .orElseThrow(() -> new ReglaDeNegocioException("Credenciales incorrectas"));
            if (!u.isActivo())
                throw new ReglaDeNegocioException("Cuenta desactivada");
            if (!passwordEncoder.matches(req.getContrasena(), u.getContrasena()))
                throw new ReglaDeNegocioException("Credenciales incorrectas");
            String token = jwtUtil.generarToken(u.getCorreo(), u.getRol().name());
            return ResponseEntity.ok(new LoginResponse(token, u.getCorreo(),
                    u.getRol().name(), u.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
