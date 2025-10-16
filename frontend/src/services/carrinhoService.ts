import api from './api';
import type {
  CarrinhoCompras,
  AdicionarProdutoAoCarrinhoRequest,
  AtualizarQuantidadeRequest,
  RemoverProdutoRequest,
  FinalizarCarrinhoRequest,
  CarrinhoResponse,
} from '../types';

export const carrinhoService = {
  // Obter carrinho do cliente
  obter: async (clienteId: string): Promise<CarrinhoCompras> => {
    const response = await api.get<CarrinhoResponse>('/carrinho', {
      params: { clienteId },
    });
    return response.data;
  },

  // Adicionar produto ao carrinho
  adicionarProduto: async (
    request: AdicionarProdutoAoCarrinhoRequest
  ): Promise<string> => {
    const response = await api.post<string>('/carrinho/adicionar', request);
    return response.data;
  },

  // Atualizar quantidade de produto
  atualizarQuantidade: async (request: AtualizarQuantidadeRequest): Promise<void> => {
    await api.put('/carrinho/atualizar', request);
  },

  // Remover produto do carrinho
  removerProduto: async (request: RemoverProdutoRequest): Promise<void> => {
    await api.delete('/carrinho/remover', { data: request });
  },

  // Finalizar carrinho (criar pedido)
  finalizar: async (request: FinalizarCarrinhoRequest): Promise<string> => {
    const response = await api.post<string>('/carrinho/finalizar', request);
    return response.data;
  },
};
