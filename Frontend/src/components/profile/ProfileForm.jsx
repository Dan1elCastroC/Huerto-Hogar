import { useState } from 'react';

const fields = [
  ['nombre', 'Nombre', 'text'],
  ['apellidos', 'Apellidos', 'text'],
  ['telefono', 'Teléfono', 'tel'],
  ['fechaNacimiento', 'Fecha de nacimiento', 'date'],
  ['direccion', 'Dirección', 'text'],
  ['region', 'Región', 'text'],
  ['comuna', 'Comuna', 'text'],
];

export default function ProfileForm({ profile, onSave, saving }) {
  const [form, setForm] = useState(profile || {});

  const actualizar = ({ target }) => setForm((current) => ({ ...current, [target.name]: target.value }));
  const enviar = (event) => {
    event.preventDefault();
    onSave(form);
  };

  return (
    <form onSubmit={enviar} className="card border-0 shadow-sm p-4 h-100">
      <h2 className="h5 text-success fw-bold">Datos personales</h2>
      <div className="row g-3">
        {fields.map(([name, label, type]) => (
          <div className={name === 'direccion' ? 'col-12' : 'col-md-6'} key={name}>
            <label htmlFor={`perfil-${name}`} className="form-label">{label}</label>
            <input id={`perfil-${name}`} name={name} type={type} className="form-control" value={form[name] || ''} onChange={actualizar} required={['nombre', 'apellidos', 'direccion', 'region', 'comuna'].includes(name)} />
          </div>
        ))}
      </div>
      <button type="submit" className="btn btn-success mt-4" disabled={saving}>{saving ? 'Guardando...' : 'Guardar cambios'}</button>
    </form>
  );
}
