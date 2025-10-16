import api from './api';
import type {
  Produto,
  Page,
  ProdutoResponse,
} from '../types';

export const produtoService = {
  // Listar todos os produtos com paginação
  listarTodos: async (page = 0, size = 12): Promise<Page<Produto>> => {
    const response = await api.get<Page<ProdutoResponse>>('/produtos', {
      params: { page, size },
    });
    return response.data;
  },

  // Buscar produtos por categoria
  buscarPorCategoria: async (
    categoria: string,
    page = 0,
    size = 12
  ): Promise<Page<Produto>> => {
    const response = await api.get<Page<ProdutoResponse>>('/produtos/categoria', {
      params: { categoria, page, size },
    });
    return response.data;
  },

  // Buscar produtos por nome
  buscarPorNome: async (nome: string, page = 0, size = 12): Promise<Page<Produto>> => {
    const response = await api.get<Page<ProdutoResponse>>('/produtos/buscar', {
      params: { nome, page, size },
    });
    return response.data;
  },

  // Buscar produto por ID
  buscarPorId: async (id: string): Promise<Produto> => {
    const response = await api.get<ProdutoResponse>(`/produtos/${id}`);
    return response.data;
  },
};
