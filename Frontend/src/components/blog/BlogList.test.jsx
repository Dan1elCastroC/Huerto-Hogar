import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import BlogList from './BlogList';

describe('BlogList', () => {
  it('muestra noticias publicadas', () => {
    render(<BlogList blogs={[{ id: 1, titulo: 'Cómo comenzar', descripcion: 'Consejos para tu huerto.' }]} loading={false} />);

    expect(screen.getByRole('heading', { name: 'Cómo comenzar' })).toBeInTheDocument();
    expect(screen.getByText('Consejos para tu huerto.')).toBeInTheDocument();
  });

  it('muestra sus estados de carga y vacío', () => {
    const { rerender } = render(<BlogList blogs={[]} loading />);
    expect(screen.getByText('Cargando noticias...')).toBeInTheDocument();

    rerender(<BlogList blogs={[]} loading={false} />);
    expect(screen.getByRole('status')).toHaveTextContent('Todavía no hay noticias publicadas.');
  });
});
