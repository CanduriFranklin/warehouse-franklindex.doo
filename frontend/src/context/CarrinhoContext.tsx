import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import { carrinhoService } from '../services/carrinhoService';

interface ItemCarrinho {
  id: string;
  produtoId: string;
  produtoNome: string;
  produtoPreco: number;
  quantidade: number;
  subtotal: number;
}

interface CarrinhoContextType {
  itens: ItemCarrinho[];
  total: number;
  loading: boolean;
  adicionarItem: (produtoId: string, quantidade: number) => Promise<void>;
  removerItem: (produtoId: string) => Promise<void>;
  atualizarQuantidade: (produtoId: string, quantidade: number) => Promise<void>;
  limparCarrinho: () => Promise<void>;
  recarregar: () => Promise<void>;
}

const CarrinhoContext = createContext<CarrinhoContextType | undefined>(undefined);

export function CarrinhoProvider({ children }: { children: ReactNode }) {
  const [itens, setItens] = useState<ItemCarrinho[]>([]);
  const [loading, setLoading] = useState(true);
  const CLIENT_ID = '0111111-1111-1111-1111-111111111111';

  const carregarCarrinho = useCallback(async () => {
    // Helper function to extract value from Dinheiro object or number
    const getValor = (preco: number | { valor: number }): number => {
      return typeof preco === 'number' ? preco : preco.valor;
    };

    try {
      const response = await carrinhoService.obter(CLIENT_ID);
      const itensAdaptados = response.itens?.map((item: { 
        id: string; 
        produto: { id: string; nome: string; preco: number | { valor: number } }; 
        quantidade: number; 
        subtotal: number | { valor: number };
      }) => ({
        id: item.id,
        produtoId: item.produto.id,
        produtoNome: item.produto.nome,
        produtoPreco: getValor(item.produto.preco),
        quantidade: item.quantidade,
        subtotal: getValor(item.subtotal)
      })) || [];
      setItens(itensAdaptados);
    } catch (error) {
      console.error('Erro ao carregar carrinho:', error);
      setItens([]);
    } finally {
      setLoading(false);
    }
  }, [CLIENT_ID]);

  useEffect(() => {
    carregarCarrinho();
  }, [carregarCarrinho]);

  const adicionarItem = async (produtoId: string, quantidade: number) => {
    try {
      await carrinhoService.adicionarProduto({ clienteId: CLIENT_ID, produtoId, quantidade });
      await carregarCarrinho();
    } catch (error) {
      console.error('Erro ao adicionar item:', error);
      throw error;
    }
  };

  const removerItem = async (produtoId: string) => {
    try {
      await carrinhoService.removerProduto({ clienteId: CLIENT_ID, produtoId });
      await carregarCarrinho();
    } catch (error) {
      console.error('Erro ao remover item:', error);
      throw error;
    }
  };

  const atualizarQuantidade = async (produtoId: string, quantidade: number) => {
    try {
      await carrinhoService.atualizarQuantidade({ clienteId: CLIENT_ID, produtoId, quantidade });
      await carregarCarrinho();
    } catch (error) {
      console.error('Erro ao atualizar quantidade:', error);
      throw error;
    }
  };

  const limparCarrinho = async () => {
    try {
      // Limpa apenas o estado local; ajuste aqui se o backend oferecer uma operação equivalente
      setItens([]);
    } catch (error) {
      console.error('Erro ao limpar carrinho:', error);
      throw error;
    }
  };

  const total = itens.reduce((sum, item) => sum + item.subtotal, 0);

  return (
    <CarrinhoContext.Provider value={{
      itens,
      total,
      loading,
      adicionarItem,
      removerItem,
      atualizarQuantidade,
      limparCarrinho,
      recarregar: carregarCarrinho
    }}>
      {children}
    </CarrinhoContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useCarrinho() {
  const context = useContext(CarrinhoContext);
  if (!context) {
    throw new Error('useCarrinho deve ser usado dentro de CarrinhoProvider');
  }
  return context;
}
