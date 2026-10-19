const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

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
    const message = typeof data === 'string' ? data : data?.message;
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
