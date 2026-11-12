const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8087';

export const API_PATH = `${API_BASE_URL}/api`;

export const MENSAJE_SIN_CONEXION = 'No se pudo conectar con el servidor. Comprueba que el backend esté activo e inténtalo de nuevo.';

// Evento global que App escucha para cerrar la sesión cuando el backend rechaza el token (401).
export const EVENTO_SESION_EXPIRADA = 'sesion-expirada';

export class ConexionError extends Error {
  constructor() {
    super(MENSAJE_SIN_CONEXION);
    this.name = 'ConexionError';
  }
}

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
  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...fetchOptions,
      headers: getHeaders(token, headers),
    });
  } catch {
    // fetch solo rechaza cuando no hay conexión con el servidor (API apagada, red caída o CORS).
    throw new ConexionError();
  }
  // Un 401 con token significa que el JWT expiró o dejó de ser válido. Sin token (por ejemplo un
  // login fallido) no hay sesión que cerrar. Un 403 es falta de rol y no cierra la sesión.
  if (response.status === 401 && token) {
    window.dispatchEvent(new Event(EVENTO_SESION_EXPIRADA));
  }
  return parseResponse(response);
};
