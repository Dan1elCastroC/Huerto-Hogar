import { render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import ReportManager from './ReportManager';
import { reporteService } from '../../services/reporteService';

vi.mock('../../services/reporteService', () => ({
  reporteService: { resumen: vi.fn(), stockCritico: vi.fn(), productosMasVendidos: vi.fn() },
}));

describe('ReportManager', () => {
  beforeEach(() => {
    reporteService.resumen.mockResolvedValue({ totalUsuarios: 12, totalProductos: 34, totalPedidos: 56 });
    reporteService.stockCritico.mockResolvedValue([{ id: 1, nombre: 'Semillas', stock: 2 }]);
    reporteService.productosMasVendidos.mockResolvedValue([{ productoId: 9, nombre: 'Tomates', unidadesVendidas: 40 }]);
  });

  it('muestra estado de carga y luego indicadores, stock crítico y más vendidos', async () => {
    render(<ReportManager token="tk" />);
    expect(screen.getByText('Cargando indicadores...')).toBeInTheDocument();
    expect(await screen.findByText('12')).toBeInTheDocument();
    expect(screen.getByText('34')).toBeInTheDocument();
    expect(screen.getByText('56')).toBeInTheDocument();
    expect(screen.getByText('Semillas: 2 unidad(es)')).toBeInTheDocument();
    expect(screen.getByText('Tomates: 40 unidad(es)')).toBeInTheDocument();
    expect(reporteService.resumen).toHaveBeenCalledWith('tk');
    expect(reporteService.stockCritico).toHaveBeenCalledWith('tk');
    expect(reporteService.productosMasVendidos).toHaveBeenCalledWith('tk');
  });

  it('muestra el error si alguna consulta falla', async () => {
    reporteService.stockCritico.mockRejectedValue(new Error('Reporte no disponible'));
    render(<ReportManager token="tk" />);
    expect(await screen.findByText('Reporte no disponible')).toHaveClass('alert-danger');
    expect(screen.queryByText('Cargando indicadores...')).not.toBeInTheDocument();
  });
});
