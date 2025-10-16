import api from './api';
import type {
  Cliente,
  CadastrarClienteRequest,
  ClienteResponse,
} from '../types';

export const clienteService = {
  // Cadastrar novo cliente
  cadastrar: async (request: CadastrarClienteRequest): Promise<Cliente> => {
    const response = await api.post<ClienteResponse>('/clientes', request);
    return response.data;
  },

  // Buscar cliente por ID
  buscarPorId: async (id: string): Promise<Cliente> => {
    const response = await api.get<ClienteResponse>(`/clientes/${id}`);
    return response.data;
  },

  // Buscar cliente por email
  buscarPorEmail: async (email: string): Promise<Cliente> => {
    const response = await api.get<ClienteResponse>('/clientes/email', {
      params: { email },
    });
    return response.data;
  },

  // Buscar cliente por CPF
  buscarPorCpf: async (cpf: string): Promise<Cliente> => {
    const response = await api.get<ClienteResponse>('/clientes/cpf', {
      params: { cpf },
    });
    return response.data;
  },
};
