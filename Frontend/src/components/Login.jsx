// src/components/Login.jsx
import React, { useState } from 'react';
import { authService } from '../services/api';

export default function Login({ onLoginSuccess, cambiarVista }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Conexión real con el backend
      const data = await authService.login({ username, password });
      onLoginSuccess(data); // Guarda token y datos de usuario
      cambiarVista('main');
    } catch (err) {
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
                <label className="form-label fw-bold">Usuario o Correo</label>
                <input 
                  type="text" 
                  className="form-control" 
                  value={username} 
                  onChange={(e) => setUsername(e.target.value)} 
                  required 
                />
              </div>
              <div className="mb-4">
                <label className="form-label fw-bold">Contraseña</label>
                <input 
                  type="password" 
                  className="form-control" 
                  value={password} 
                  onChange={(e) => setPassword(e.target.value)} 
                  required 
                />
              </div>
              <button type="submit" className="btn btn-success w-100 fw-bold py-2" disabled={loading}>
                {loading ? "Conectando..." : "Ingresar"}
              </button>
            </form>
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