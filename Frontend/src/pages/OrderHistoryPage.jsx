import { useEffect, useState } from 'react';
import OrderList from '../components/orders/OrderList';
import { pedidoService } from '../services/pedidoService';

export default function OrderHistoryPage({ user }) {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    pedidoService.historial(user.token)
      .then(setPedidos)
      .catch((requestError) => setError(requestError.message || 'No se pudo cargar el historial.'))
      .finally(() => setLoading(false));
  }, [user]);

  return (
    <main className="container my-5">
      <h1 className="text-success fw-bold mb-4">Mis pedidos</h1>
      <OrderList pedidos={pedidos} loading={loading} error={error} />
    </main>
  );
}
