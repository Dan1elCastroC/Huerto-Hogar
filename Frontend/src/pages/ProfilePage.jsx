import { useEffect, useState } from 'react';
import PasswordForm from '../components/profile/PasswordForm';
import ProfileForm from '../components/profile/ProfileForm';
import { usuarioService } from '../services/usuarioService';

export default function ProfilePage({ user, onUserUpdated }) {
  const [profile, setProfile] = useState(user || {});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [estado, setEstado] = useState(null);

  useEffect(() => {
    usuarioService.getProfile(user.token)
      .then((data) => setProfile({ ...user, ...data }))
      .catch((error) => setEstado({ tipo: 'danger', texto: error.message || 'No se pudo cargar tu perfil.' }))
      .finally(() => setLoading(false));
  }, [user]);

  const guardarPerfil = async (data) => {
    setSaving(true);
    setEstado(null);
    try {
      const updated = await usuarioService.updateProfile(data, user.token);
      const nextUser = { ...user, ...updated };
      setProfile(nextUser);
      onUserUpdated?.(nextUser);
      setEstado({ tipo: 'success', texto: 'Perfil actualizado correctamente.' });
    } catch (error) {
      setEstado({ tipo: 'danger', texto: error.message || 'No se pudo actualizar el perfil.' });
    } finally {
      setSaving(false);
    }
  };

  const cambiarContrasena = async (data, reset) => {
    setSaving(true);
    setEstado(null);
    try {
      await usuarioService.changePassword(data, user.token);
      reset();
      setEstado({ tipo: 'success', texto: 'Contraseña actualizada correctamente.' });
    } catch (error) {
      setEstado({ tipo: 'danger', texto: error.message || 'No se pudo actualizar la contraseña.' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="container my-5 text-center">Cargando perfil...</div>;

  return (
    <main className="container my-5">
      <h1 className="text-success fw-bold mb-4">Mi perfil</h1>
      {estado && <div className={`alert alert-${estado.tipo}`} role="alert">{estado.texto}</div>}
      <div className="row g-4">
        <div className="col-lg-7"><ProfileForm profile={profile} onSave={guardarPerfil} saving={saving} /></div>
        <div className="col-lg-5"><PasswordForm onSave={cambiarContrasena} saving={saving} /></div>
      </div>
    </main>
  );
}
