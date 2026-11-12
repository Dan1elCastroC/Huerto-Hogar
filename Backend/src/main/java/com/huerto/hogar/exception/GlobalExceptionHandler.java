package com.huerto.hogar.exception;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> notFound(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(404).body(ApiError.builder().status(404).error("Not Found").mensaje(ex.getMessage()).build());
    }
    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<ApiError> badRequest(ReglaDeNegocioException ex) {
        return ResponseEntity.status(400).body(ApiError.builder().status(400).error("Bad Request").mensaje(ex.getMessage()).build());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex) {
        List<String> d = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
        return ResponseEntity.status(400).body(ApiError.builder().status(400).error("Validacion").mensaje("Datos invalidos").detalles(d).build());
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> forbidden(AccessDeniedException ex) {
        // Un anónimo que llega a un @PreAuthorize no está "sin permiso": le falta autenticarse (401).
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body(ApiError.builder().status(401).error("No autenticado")
                .mensaje("Debes iniciar sesión para acceder a este recurso").build());
        }
        return ResponseEntity.status(403).body(ApiError.builder().status(403).error("Forbidden").mensaje("Sin permiso").build());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> general(Exception ex) {
        return ResponseEntity.status(500).body(ApiError.builder().status(500).error("Error").mensaje(ex.getMessage()).build());
    }
}
