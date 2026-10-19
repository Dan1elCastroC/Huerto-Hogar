import { useEffect, useState } from 'react';
import { contactoService } from '../../services/contactoService';

export default function ContactManager({ token }) {
  const [contactos, setContactos] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    contactoService.getAll(token).then(setContactos).catch((requestError) => setError(requestError.message));
  }, [token]);

  const marcarLeido = async (id) => {
    try { const updated = await contactoService.markRead(id, token); setContactos((current) => current.map((item) => item.id === id ? updated : item)); }
    catch (requestError) { setError(requestError.message); }
  };

  return <section><h2 className="h4 text-success fw-bold">Mensajes de contacto</h2>{error && <div className="alert alert-danger">{error}</div>}<div className="table-responsive"><table className="table table-hover align-middle"><thead><tr><th>Nombre</th><th>Correo</th><th>Mensaje</th><th>Estado</th><th /></tr></thead><tbody>{contactos.map((item) => <tr key={item.id} className={item.leido ? '' : 'table-warning'}><td>{item.nombre}</td><td>{item.correo}</td><td>{item.comentario}</td><td>{item.leido ? 'Leído' : 'Pendiente'}</td><td>{!item.leido && <button className="btn btn-sm btn-outline-success" onClick={() => marcarLeido(item.id)}>Marcar leído</button>}</td></tr>)}</tbody></table></div></section>;
}
