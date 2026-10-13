import React, { useState } from 'react';
import { authService } from '../services/api';

const initialForm = {
  run: '',
  nombre: '',
  apellidos: '',
  correo: '',
  contrasena: '',
  confirmarContrasena: '',
  telefono: '',
  fechaNacimiento: '',
  direccion: '',
  region: '',
  comuna: '',
};

export default function Registro({ cambiarVista }) {
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [registrado, setRegistrado] = useState(false);

  const actualizar = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    if (form.contrasena !== form.confirmarContrasena) {
      setError('Las contraseñas no coinciden.');
      return;
    }

    setLoading(true);
    try {
      await authService.registro(form);
      setRegistrado(true);
    } catch (requestError) {
      setError(requestError.message || 'No se pudo completar el registro.');
    } finally {
      setLoading(false);
    }
  };

  if (registrado) {
    return (
      <div className="container my-5 py-5">
        <div className="alert alert-success shadow-sm mx-auto text-center" style={{ maxWidth: 620 }}>
          <h2 className="h4 fw-bold">¡Registro completado!</h2>
          <p className="mb-3">Tu cuenta ya está lista. Ingresa para comenzar a comprar.</p>
          <button type="button" className="btn btn-success" onClick={() => cambiarVista('login')}>
            Ir a iniciar sesión
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="container my-5">
      <div className="card shadow border-0 rounded-4 p-4 mx-auto" style={{ maxWidth: 900 }}>
        <h2 className="text-center fw-bold text-success mb-2">Crear cuenta</h2>
        <p className="text-center text-muted mb-4">Regístrate para guardar tu carrito y confirmar tus compras.</p>
        {error && <div className="alert alert-danger" role="alert">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="row g-3">
            <div className="col-md-4">
              <label htmlFor="registro-run" className="form-label">RUN</label>
              <input id="registro-run" name="run" className="form-control" value={form.run} onChange={actualizar} placeholder="190110222" minLength="7" maxLength="9" required />
            </div>
            <div className="col-md-4">
              <label htmlFor="registro-nombre" className="form-label">Nombre</label>
              <input id="registro-nombre" name="nombre" className="form-control" value={form.nombre} onChange={actualizar} required />
            </div>
            <div className="col-md-4">
              <label htmlFor="registro-apellidos" className="form-label">Apellidos</label>
              <input id="registro-apellidos" name="apellidos" className="form-control" value={form.apellidos} onChange={actualizar} required />
            </div>
            <div className="col-md-6">
              <label htmlFor="registro-correo" className="form-label">Correo electrónico</label>
              <input id="registro-correo" name="correo" type="email" className="form-control" value={form.correo} onChange={actualizar} placeholder="tu-correo@gmail.com" autoComplete="email" required />
            </div>
            <div className="col-md-3">
              <label htmlFor="registro-contrasena" className="form-label">Contraseña</label>
              <input id="registro-contrasena" name="contrasena" type="password" className="form-control" value={form.contrasena} onChange={actualizar} minLength="4" maxLength="10" autoComplete="new-password" required />
            </div>
            <div className="col-md-3">
              <label htmlFor="registro-confirmar" className="form-label">Repetir contraseña</label>
              <input id="registro-confirmar" name="confirmarContrasena" type="password" className="form-control" value={form.confirmarContrasena} onChange={actualizar} minLength="4" maxLength="10" autoComplete="new-password" required />
            </div>
            <div className="col-md-4">
              <label htmlFor="registro-telefono" className="form-label">Teléfono</label>
              <input id="registro-telefono" name="telefono" type="tel" className="form-control" value={form.telefono} onChange={actualizar} />
            </div>
            <div className="col-md-4">
              <label htmlFor="registro-fecha" className="form-label">Fecha de nacimiento</label>
              <input id="registro-fecha" name="fechaNacimiento" type="date" className="form-control" value={form.fechaNacimiento} onChange={actualizar} />
            </div>
            <div className="col-md-4">
              <label htmlFor="registro-region" className="form-label">Región</label>
              <input id="registro-region" name="region" className="form-control" value={form.region} onChange={actualizar} required />
            </div>
            <div className="col-md-6">
              <label htmlFor="registro-comuna" className="form-label">Comuna</label>
              <input id="registro-comuna" name="comuna" className="form-control" value={form.comuna} onChange={actualizar} required />
            </div>
            <div className="col-md-6">
              <label htmlFor="registro-direccion" className="form-label">Dirección de entrega</label>
              <input id="registro-direccion" name="direccion" className="form-control" value={form.direccion} onChange={actualizar} required />
            </div>
          </div>

          <div className="d-flex justify-content-between align-items-center gap-2 mt-4">
            <button type="button" className="btn btn-link text-muted" onClick={() => cambiarVista('login')}>Ya tengo una cuenta</button>
            <button type="submit" className="btn btn-success px-4" disabled={loading}>
              {loading ? 'Registrando...' : 'Crear cuenta'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
