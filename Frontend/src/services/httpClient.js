const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8087';

export const API_PATH = `${API_BASE_URL}/api`;

export const getHeaders = (token, extraHeaders = {}) => ({
  'Content-Type': 'application/json',
  ...extraHeaders,
  ...(token ? { Authorization: `Bearer ${token}` } : {}),
});

export const parseResponse = async (response) => {
  const contentType = response.headers.get('content-type') || '';
  const data = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    let message;
    if (typeof data === 'string') {
      message = data;
    } else if (data?.mensaje || data?.message) {
      // GlobalExceptionHandler devuelve { mensaje, detalles[] }
      const detalles = Array.isArray(data.detalles) && data.detalles.length
        ? `: ${data.detalles.join('. ')}`
        : '';
      message = `${data.mensaje || data.message}${detalles}`;
    }
    if (!message && (response.status === 401 || response.status === 403)) {
      message = 'No tienes permiso o tu sesión expiró. Inicia sesión nuevamente.';
    }
    throw new Error(message || 'La solicitud no pudo completarse');
  }

  return data;
};

export const request = async (path, options = {}) => {
  const { token, headers, ...fetchOptions } = options;
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...fetchOptions,
    headers: getHeaders(token, headers),
  });
  return parseResponse(response);
};
