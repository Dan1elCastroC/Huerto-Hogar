import { useState } from 'react';

const initialForm = { contrasenaActual: '', nuevaContrasena: '', confirmarContrasena: '' };

export default function PasswordForm({ onSave, saving }) {
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState('');

  const actualizar = ({ target }) => setForm((current) => ({ ...current, [target.name]: target.value }));

  const enviar = (event) => {
    event.preventDefault();
    if (form.nuevaContrasena !== form.confirmarContrasena) {
      setError('Las contraseñas nuevas no coinciden.');
      return;
    }
    setError('');
    onSave(form, () => setForm(initialForm));
  };

  return (
    <form onSubmit={enviar} className="card border-0 shadow-sm p-4 h-100">
      <h2 className="h5 text-success fw-bold">Cambiar contraseña</h2>
      {error && <div className="alert alert-danger" role="alert">{error}</div>}
      {['contrasenaActual', 'nuevaContrasena', 'confirmarContrasena'].map((name) => (
        <div className="mb-3" key={name}>
          <label htmlFor={`perfil-${name}`} className="form-label">{name === 'contrasenaActual' ? 'Contraseña actual' : name === 'nuevaContrasena' ? 'Nueva contraseña' : 'Confirmar contraseña'}</label>
          <input id={`perfil-${name}`} name={name} type="password" className="form-control" value={form[name]} onChange={actualizar} minLength="4" maxLength="10" required />
        </div>
      ))}
      <button type="submit" className="btn btn-outline-success mt-auto" disabled={saving}>{saving ? 'Actualizando...' : 'Actualizar contraseña'}</button>
    </form>
  );
}
