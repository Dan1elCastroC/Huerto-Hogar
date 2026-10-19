import { useEffect, useState } from 'react';
import { usuarioService } from '../../services/usuarioService';

export default function UserManager({ token }) {
  const [usuarios, setUsuarios] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    usuarioService.getAll(token).then(setUsuarios).catch((requestError) => setError(requestError.message));
  }, [token]);

  const toggle = async (id) => {
    try { const updated = await usuarioService.toggleActive(id, token); setUsuarios((current) => current.map((item) => item.id === id ? updated : item)); }
    catch (requestError) { setError(requestError.message); }
  };

  return <section><h2 className="h4 text-success fw-bold">Usuarios</h2>{error && <div className="alert alert-danger">{error}</div>}<div className="table-responsive"><table className="table table-hover"><thead><tr><th>Nombre</th><th>Correo</th><th>Rol</th><th>Estado</th><th /></tr></thead><tbody>{usuarios.map((item) => <tr key={item.id}><td>{item.nombre} {item.apellidos}</td><td>{item.correo}</td><td>{item.rol}</td><td>{item.activo ? 'Activo' : 'Inactivo'}</td><td><button className="btn btn-sm btn-outline-secondary" onClick={() => toggle(item.id)}>{item.activo ? 'Desactivar' : 'Activar'}</button></td></tr>)}</tbody></table></div></section>;
}
