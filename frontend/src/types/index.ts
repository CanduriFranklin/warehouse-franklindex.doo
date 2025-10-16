// Tipos de domínio do Storefront

export interface Dinheiro {
  valor: number;
  moeda: string;
}

export interface Endereco {
  rua: string;
  numero: string;
  complemento?: string;
  bairro: string;
  cidade: string;
  estado: string;
  cep: string;
}

export interface InformacaoPagamento {
  tipo: 'CARTAO_CREDITO' | 'CARTAO_DEBITO' | 'PIX' | 'BOLETO';
  numeroCartao?: string;
  nomeCartao?: string;
  validadeCartao?: string;
  cvv?: string;
  chavePix?: string;
}

export interface Produto {
  id: string;
  nome: string;
  descricao: string;
  preco: Dinheiro;
  categoria: string;
  imagemUrl?: string;
  quantidadeEstoque: number;
  ativo: boolean;
}

export interface Cliente {
  id: string;
  nome: string;
  email: string;
  cpf: string;
  telefone?: string;
  endereco: Endereco;
}

export interface ItemCarrinho {
  id: string;
  produto: Produto;
  quantidade: number;
  precoUnitario: Dinheiro;
  subtotal: Dinheiro;
}

export interface CarrinhoCompras {
  id: string;
  cliente: Cliente;
  itens: ItemCarrinho[];
  valorTotal: Dinheiro;
  quantidadeTotal: number;
  status: 'ATIVO' | 'FINALIZADO' | 'ABANDONADO';
  criadoEm: string;
  atualizadoEm: string;
}

export interface ItemPedido {
  id: string;
  produtoId: string;
  nomeProduto: string;
  quantidade: number;
  precoUnitario: Dinheiro;
  subtotal: Dinheiro;
}

export interface Pedido {
  id: string;
  numeroPedido: string;
  cliente: Cliente;
  itens: ItemPedido[];
  valorTotal: Dinheiro;
  status: 'AGUARDANDO_PAGAMENTO' | 'PAGAMENTO_CONFIRMADO' | 'EM_PREPARACAO' | 'ENVIADO' | 'ENTREGUE' | 'CANCELADO' | 'DEVOLVIDO' | 'FINALIZADO';
  enderecoEntrega: Endereco;
  informacaoPagamento: InformacaoPagamento;
  observacoes?: string;
  criadoEm: string;
  atualizadoEm: string;
}

// DTOs para requisições
export interface AdicionarProdutoAoCarrinhoRequest {
  clienteId: string;
  produtoId: string;
  quantidade: number;
}

export interface AtualizarQuantidadeRequest {
  clienteId: string;
  produtoId: string;
  quantidade: number;
}

export interface RemoverProdutoRequest {
  clienteId: string;
  produtoId: string;
}

export interface FinalizarCarrinhoRequest {
  clienteId: string;
  enderecoEntrega: Endereco;
  informacaoPagamento: InformacaoPagamento;
  observacoes?: string;
}

export interface CadastrarClienteRequest {
  nome: string;
  email: string;
  cpf: string;
  telefone?: string;
  endereco: Endereco;
}

// Tipos para paginação
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Tipos de resposta
export interface ProdutoResponse extends Produto {}
export interface ClienteResponse extends Cliente {}
export interface CarrinhoResponse extends CarrinhoCompras {}
export interface PedidoResponse extends Pedido {}
