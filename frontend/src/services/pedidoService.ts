import api from './api';
import type { Pedido, Page, PedidoResponse } from '../types';

export const pedidoService = {
  // Buscar pedido por ID
  buscarPorId: async (id: string): Promise<Pedido> => {
    const response = await api.get<PedidoResponse>(`/pedidos/${id}`);
    return response.data;
  },

  // Buscar pedido por número
  buscarPorNumero: async (numeroPedido: string): Promise<Pedido> => {
    const response = await api.get<PedidoResponse>('/pedidos/numero', {
      params: { numeroPedido },
    });
    return response.data;
  },

  // Listar pedidos do cliente
  listarPorCliente: async (
    clienteId: string,
    page = 0,
    size = 10
  ): Promise<Page<Pedido>> => {
    const response = await api.get<Page<PedidoResponse>>('/pedidos/cliente', {
      params: { clienteId, page, size },
    });
    return response.data;
  },
};
