import { useEffect, useState } from 'react';
import { contactoService } from '../../services/contactoService';

const emptyForm = { nombre: '', correo: '', comentario: '' };

export default function ContactoForm({ user, onSubmitted }) {
  const [form, setForm] = useState(emptyForm);
  const [estado, setEstado] = useState(null);
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    if (!user) return;
    setForm((current) => ({
      ...current,
      nombre: current.nombre || user.nombre || user.correo || '',
      correo: current.correo || user.correo || '',
    }));
  }, [user]);

  const actualizar = ({ target }) => {
    setForm((current) => ({ ...current, [target.name]: target.value }));
  };

  const enviar = async (event) => {
    event.preventDefault();
    setEstado(null);
    setEnviando(true);
    try {
      await contactoService.create(form, user?.token);
      setForm(emptyForm);
      setEstado({ tipo: 'success', texto: 'Tu mensaje fue enviado correctamente.' });
      onSubmitted?.();
    } catch (error) {
      setEstado({ tipo: 'danger', texto: error.message || 'No se pudo enviar el mensaje.' });
    } finally {
      setEnviando(false);
    }
  };

  return (
    <form onSubmit={enviar} className="tarjeta-form">
      {estado && <div className={`alert alert-${estado.tipo}`} role="alert">{estado.texto}</div>}
      <div className="mb-3">
        <label htmlFor="contacto-nombre" className="form-label">Nombre</label>
        <input id="contacto-nombre" name="nombre" className="form-control" value={form.nombre} onChange={actualizar} maxLength="100" required />
      </div>
      <div className="mb-3">
        <label htmlFor="contacto-correo" className="form-label">Correo electrónico</label>
        <input id="contacto-correo" name="correo" type="email" className="form-control" value={form.correo} onChange={actualizar} maxLength="100" required />
      </div>
      <div className="mb-3">
        <label htmlFor="contacto-comentario" className="form-label">Mensaje</label>
        <textarea id="contacto-comentario" name="comentario" className="form-control" rows="5" value={form.comentario} onChange={actualizar} maxLength="500" required />
        <small className="text-muted">{form.comentario.length}/500 caracteres</small>
      </div>
      <button type="submit" className="btn btn-success" disabled={enviando}>
        {enviando ? 'Enviando...' : 'Enviar mensaje'}
      </button>
    </form>
  );
}
