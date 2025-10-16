import type { Dinheiro } from '../types';

/**
 * Formata valor monetário para exibição
 * @param dinheiro Objeto Dinheiro
 * @returns String formatada (ex: "R$ 29,90")
 */
export const formatarDinheiro = (dinheiro: Dinheiro | undefined): string => {
  if (!dinheiro) return 'R$ 0,00';
  
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: dinheiro.moeda || 'BRL',
  }).format(dinheiro.valor);
};

/**
 * Formata CEP para exibição
 * @param cep CEP sem formatação
 * @returns CEP formatado (ex: "12345-678")
 */
export const formatarCEP = (cep: string): string => {
  return cep.replace(/^(\d{5})(\d{3})$/, '$1-$2');
};

/**
 * Formata CPF para exibição
 * @param cpf CPF sem formatação
 * @returns CPF formatado (ex: "123.456.789-00")
 */
export const formatarCPF = (cpf: string): string => {
  return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4');
};

/**
 * Formata telefone para exibição
 * @param telefone Telefone sem formatação
 * @returns Telefone formatado (ex: "(11) 98765-4321")
 */
export const formatarTelefone = (telefone: string): string => {
  if (telefone.length === 11) {
    return telefone.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3');
  }
  return telefone.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3');
};

/**
 * Formata data para exibição
 * @param data Data em formato ISO
 * @returns Data formatada (ex: "16/10/2025 14:30")
 */
export const formatarData = (data: string): string => {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(data));
};

/**
 * Valida CPF
 * @param cpf CPF a ser validado
 * @returns true se válido
 */
export const validarCPF = (cpf: string): boolean => {
  cpf = cpf.replace(/[^\d]/g, '');
  
  if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) {
    return false;
  }
  
  let soma = 0;
  for (let i = 0; i < 9; i++) {
    soma += parseInt(cpf.charAt(i)) * (10 - i);
  }
  let resto = 11 - (soma % 11);
  let digito = resto >= 10 ? 0 : resto;
  
  if (digito !== parseInt(cpf.charAt(9))) {
    return false;
  }
  
  soma = 0;
  for (let i = 0; i < 10; i++) {
    soma += parseInt(cpf.charAt(i)) * (11 - i);
  }
  resto = 11 - (soma % 11);
  digito = resto >= 10 ? 0 : resto;
  
  return digito === parseInt(cpf.charAt(10));
};

/**
 * Valida CEP
 * @param cep CEP a ser validado
 * @returns true se válido
 */
export const validarCEP = (cep: string): boolean => {
  cep = cep.replace(/[^\d]/g, '');
  return /^\d{8}$/.test(cep);
};

/**
 * Valida email
 * @param email Email a ser validado
 * @returns true se válido
 */
export const validarEmail = (email: string): boolean => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
};

/**
 * Traduz status do pedido para português
 * @param status Status do pedido
 * @returns Status em português
 */
export const traduzirStatusPedido = (status: string): string => {
  const statusMap: Record<string, string> = {
    'AGUARDANDO_PAGAMENTO': 'Aguardando Pagamento',
    'PAGAMENTO_CONFIRMADO': 'Pagamento Confirmado',
    'EM_PREPARACAO': 'Em Preparação',
    'ENVIADO': 'Enviado',
    'ENTREGUE': 'Entregue',
    'CANCELADO': 'Cancelado',
    'DEVOLVIDO': 'Devolvido',
    'FINALIZADO': 'Finalizado',
  };
  return statusMap[status] || status;
};

/**
 * Traduz status do carrinho para português
 * @param status Status do carrinho
 * @returns Status em português
 */
export const traduzirStatusCarrinho = (status: string): string => {
  const statusMap: Record<string, string> = {
    'ATIVO': 'Ativo',
    'FINALIZADO': 'Finalizado',
    'ABANDONADO': 'Abandonado',
  };
  return statusMap[status] || status;
};
