import { request } from './httpClient';

export const regionService = {
  getAll: () => request('/api/regiones'),
  getCommunes: (region) => request(`/api/regiones/${encodeURIComponent(region)}/comunas`),
  getAllWithCommunes: () => request('/api/regiones/todas'),
};

export default regionService;
