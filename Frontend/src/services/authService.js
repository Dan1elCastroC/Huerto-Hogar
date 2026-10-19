import { request } from './httpClient';

export const authService = {
  login: (credentials) => request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ correo: credentials.correo, contrasena: credentials.contrasena }),
  }),

  registro: (userData) => request('/api/auth/registro', {
    method: 'POST',
    body: JSON.stringify(userData),
  }),

  getPerfil: (token) => request('/api/usuarios/perfil', { token }),
};

export default authService;
