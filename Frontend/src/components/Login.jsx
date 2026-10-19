// src/components/Login.jsx
import React, { useState } from 'react';
import { authService } from '../services/authService';

export default function Login({ onLoginSuccess, cambiarVista }) {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Conexión real con el backend
      const data = await authService.login({ correo, contrasena });
      let profile = {};
      try {
        profile = await authService.getPerfil(data.token);
      } catch {
        // El inicio de sesión sigue siendo válido aunque el perfil no esté disponible.
      }
      onLoginSuccess({ ...data, ...profile }); // Guarda token y datos de usuario
    } catch {
      setError("Usuario o contraseña incorrectos. Intente nuevamente.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container my-5 py-5">
      <div className="row justify-content-center">
        <div className="col-md-5">
          <div className="card shadow border-0 p-4 rounded-4">
            <h2 className="text-center fw-bold text-success mb-4">Iniciar Sesión</h2>
            {error && <div className="alert alert-danger">{error}</div>}
            
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label htmlFor="login-correo" className="form-label fw-bold">Correo electrónico</label>
                <input 
                  id="login-correo"
                  type="text" 
                  className="form-control" 
                  value={correo} 
                  onChange={(e) => setCorreo(e.target.value)} 
                  autoComplete="email"
                  required 
                />
              </div>
              <div className="mb-4">
                <label htmlFor="login-contrasena" className="form-label fw-bold">Contraseña</label>
                <input 
                  id="login-contrasena"
                  type="password" 
                  className="form-control" 
                  value={contrasena} 
                  onChange={(e) => setContrasena(e.target.value)} 
                  autoComplete="current-password"
                  required 
                />
              </div>
              <button type="submit" className="btn btn-success w-100 fw-bold py-2" disabled={loading}>
                {loading ? "Conectando..." : "Ingresar"}
              </button>
            </form>
            <p className="text-center text-muted mt-3 mb-0">
              ¿Aún no tienes cuenta?{' '}
              <button type="button" className="btn btn-link p-0 align-baseline" onClick={() => cambiarVista('registro')}>
                Regístrate aquí
              </button>
            </p>
            <div className="text-center mt-3">
              <button className="btn btn-link text-muted text-decoration-none" onClick={() => cambiarVista('landing')}>
                ← Volver al inicio
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
