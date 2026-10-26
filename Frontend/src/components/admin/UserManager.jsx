import { useEffect, useState } from 'react';
import { usuarioService } from '../../services/usuarioService';
import { pedidoService } from '../../services/pedidoService';
import OrderList from '../orders/OrderList';

const initialForm = { run: '', nombre: '', apellidos: '', correo: '', contrasena: '', confirmarContrasena: '', telefono: '', fechaNacimiento: '', direccion: '', region: '', comuna: '', rol: 'CLIENTE' };
const fields = [['run', 'RUN', 'text'], ['nombre', 'Nombre', 'text'], ['apellidos', 'Apellidos', 'text'], ['correo', 'Correo electrónico', 'email'], ['contrasena', 'Contraseña', 'password'], ['confirmarContrasena', 'Confirmar contraseña', 'password'], ['telefono', 'Teléfono', 'tel'], ['fechaNacimiento', 'Fecha de nacimiento', 'date'], ['direccion', 'Dirección', 'text'], ['region', 'Región', 'text'], ['comuna', 'Comuna', 'text']];

export default function UserManager({ token }) {
  const [usuarios, setUsuarios] = useState([]);
  const [error, setError] = useState('');
  const [form, setForm] = useState(null);
  const [editando, setEditando] = useState(null);
  const [guardando, setGuardando] = useState(false);
  const [mensaje, setMensaje] = useState('');
  const [usuarioHistorial, setUsuarioHistorial] = useState(null);
  const [historial, setHistorial] = useState([]);
  const [cargandoHistorial, setCargandoHistorial] = useState(false);
  const [errorHistorial, setErrorHistorial] = useState('');

  useEffect(() => {
    usuarioService.getAll(token).then(setUsuarios).catch((requestError) => setError(requestError.message));
  }, [token]);

  const toggle = async (id) => {
    try { const updated = await usuarioService.toggleActive(id, token); setUsuarios((current) => current.map((item) => item.id === id ? updated : item)); }
    catch (requestError) { setError(requestError.message); }
  };

  const editar = (usuario) => {
    setForm(Object.fromEntries(Object.keys(initialForm).map((campo) => [campo, campo === 'contrasena' || campo === 'confirmarContrasena' ? '' : usuario[campo] ?? initialForm[campo]])));
    setEditando(usuario.id);
    setError('');
    setMensaje('');
  };

  const guardar = async (event) => {
    event.preventDefault();
    setError('');
    if (form.contrasena !== form.confirmarContrasena) { setError('Las contraseñas no coinciden.'); return; }
    setGuardando(true);
    try {
      const payload = { ...form, contrasena: form.contrasena || null, confirmarContrasena: form.confirmarContrasena || null };
      const guardado = editando === null ? await usuarioService.create(payload, token) : await usuarioService.update(editando, payload, token);
      setUsuarios((current) => editando === null ? [...current, guardado] : current.map((item) => item.id === editando ? guardado : item));
      setMensaje(editando === null ? 'Usuario creado correctamente.' : 'Usuario actualizado correctamente.');
      setForm(null);
      setEditando(null);
    } catch (requestError) { setError(requestError.message); }
    finally { setGuardando(false); }
  };

  const verHistorial = async (usuario) => {
    setUsuarioHistorial(usuario);
    setHistorial([]);
    setErrorHistorial('');
    setCargandoHistorial(true);
    try { const pedidos = await pedidoService.getAll(token); setHistorial(pedidos.filter((pedido) => pedido.usuarioId === usuario.id)); }
    catch (requestError) { setErrorHistorial(requestError.message); }
    finally { setCargandoHistorial(false); }
  };

  return <section>
    <div className="d-flex justify-content-between align-items-center mb-3"><h2 className="h4 text-success fw-bold">Usuarios</h2><button type="button" className="btn btn-success" onClick={() => { setForm(initialForm); setEditando(null); setError(''); setMensaje(''); }}>Nuevo usuario</button></div>
    {error && <div className="alert alert-danger" role="alert">{error}</div>}
    {mensaje && <div className="alert alert-success" role="status">{mensaje}</div>}
    {form && <form onSubmit={guardar} className="card p-4 mb-4">
      <h3 className="h5">{editando === null ? 'Crear usuario' : 'Editar usuario'}</h3>
      <div className="row g-3">{fields.map(([name, label, type]) => <div className="col-md-6" key={name}>
        <label className="form-label" htmlFor={`usuario-${name}`}>{name === 'contrasena' && editando !== null ? 'Nueva contraseña' : label}</label>
        <input id={`usuario-${name}`} name={name} type={type} className="form-control" value={form[name]} onChange={(event) => setForm({ ...form, [name]: event.target.value })} readOnly={name === 'run' && editando !== null} required={!['telefono', 'fechaNacimiento'].includes(name) && (type !== 'password' || editando === null)} minLength={type === 'password' ? 4 : name === 'run' ? 7 : undefined} maxLength={type === 'password' ? 10 : name === 'run' ? 9 : undefined} />
      </div>)}<div className="col-md-6"><label className="form-label" htmlFor="usuario-rol">Rol</label><select id="usuario-rol" className="form-select" value={form.rol} onChange={(event) => setForm({ ...form, rol: event.target.value })}>{['CLIENTE', 'VENDEDOR', 'ADMINISTRADOR'].map((rol) => <option key={rol}>{rol}</option>)}</select></div></div>
      {editando !== null && <p className="text-muted mt-3">Deja las contraseñas vacías para conservar la actual.</p>}
      <div className="d-flex gap-2 mt-3"><button type="submit" className="btn btn-success" disabled={guardando}>{guardando ? 'Guardando...' : editando === null ? 'Crear usuario' : 'Guardar cambios'}</button><button type="button" className="btn btn-outline-secondary" disabled={guardando} onClick={() => { setForm(null); setEditando(null); }}>Cancelar</button></div>
    </form>}
    <div className="table-responsive"><table className="table table-hover"><thead><tr><th>Nombre</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>{usuarios.map((item) => <tr key={item.id}><td>{item.nombre} {item.apellidos}</td><td>{item.correo}</td><td>{item.rol}</td><td>{item.activo ? 'Activo' : 'Inactivo'}</td><td className="d-flex gap-2 flex-wrap"><button type="button" className="btn btn-sm btn-outline-success" aria-label={`Editar ${item.nombre} ${item.apellidos}`} onClick={() => editar(item)}>Editar</button><button type="button" className="btn btn-sm btn-outline-primary" aria-label={`Historial de ${item.nombre} ${item.apellidos}`} disabled={cargandoHistorial} onClick={() => verHistorial(item)}>Historial de compras</button><button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => toggle(item.id)}>{item.activo ? 'Desactivar' : 'Activar'}</button></td></tr>)}</tbody></table></div>
    {usuarioHistorial && <div className="card p-4 mt-3"><h3 className="h5">Historial de compras de {usuarioHistorial.nombre} {usuarioHistorial.apellidos}</h3><OrderList pedidos={historial} loading={cargandoHistorial} error={errorHistorial} /><button type="button" className="btn btn-outline-secondary mt-3 align-self-start" onClick={() => setUsuarioHistorial(null)}>Cerrar historial</button></div>}
  </section>;
}
