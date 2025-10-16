import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import type { CarrinhoCompras, Produto } from '../types';
import { carrinhoService } from '../services/carrinhoService';

interface CarrinhoContextData {
  carrinho: CarrinhoCompras | null;
  loading: boolean;
  error: string | null;
  adicionarProduto: (produto: Produto, quantidade: number) => Promise<void>;
  removerProduto: (produtoId: string) => Promise<void>;
  atualizarQuantidade: (produtoId: string, quantidade: number) => Promise<void>;
  atualizarCarrinho: () => Promise<void>;
  limparCarrinho: () => void;
}

const CarrinhoContext = createContext<CarrinhoContextData>({} as CarrinhoContextData);

export const useCarrinho = () => {
  const context = useContext(CarrinhoContext);
  if (!context) {
    throw new Error('useCarrinho deve ser usado dentro de CarrinhoProvider');
  }
  return context;
};

interface CarrinhoProviderProps {
  children: ReactNode;
}

export const CarrinhoProvider: React.FC<CarrinhoProviderProps> = ({ children }) => {
  const [carrinho, setCarrinho] = useState<CarrinhoCompras | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Para demo: cliente fixo (em produção viria de autenticação)
  const CLIENT_ID = localStorage.getItem('clienteId') || 'demo-client-id';

  useEffect(() => {
    if (CLIENT_ID && CLIENT_ID !== 'demo-client-id') {
      atualizarCarrinho();
    }
  }, [CLIENT_ID]);

  const atualizarCarrinho = async () => {
    try {
      setLoading(true);
      setError(null);
      const carrinhoAtualizado = await carrinhoService.obter(CLIENT_ID);
      setCarrinho(carrinhoAtualizado);
    } catch (err) {
      if ((err as any)?.response?.status === 404) {
        // Carrinho não existe ainda
        setCarrinho(null);
      } else {
        setError('Erro ao carregar carrinho');
        console.error(err);
      }
    } finally {
      setLoading(false);
    }
  };

  const adicionarProduto = async (produto: Produto, quantidade: number) => {
    try {
      setLoading(true);
      setError(null);
      await carrinhoService.adicionarProduto({
        clienteId: CLIENT_ID,
        produtoId: produto.id,
        quantidade,
      });
      await atualizarCarrinho();
    } catch (err) {
      setError('Erro ao adicionar produto ao carrinho');
      console.error(err);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const removerProduto = async (produtoId: string) => {
    try {
      setLoading(true);
      setError(null);
      await carrinhoService.removerProduto({
        clienteId: CLIENT_ID,
        produtoId,
      });
      await atualizarCarrinho();
    } catch (err) {
      setError('Erro ao remover produto do carrinho');
      console.error(err);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const atualizarQuantidade = async (produtoId: string, quantidade: number) => {
    try {
      setLoading(true);
      setError(null);
      await carrinhoService.atualizarQuantidade({
        clienteId: CLIENT_ID,
        produtoId,
        quantidade,
      });
      await atualizarCarrinho();
    } catch (err) {
      setError('Erro ao atualizar quantidade');
      console.error(err);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const limparCarrinho = () => {
    setCarrinho(null);
  };

  return (
    <CarrinhoContext.Provider
      value={{
        carrinho,
        loading,
        error,
        adicionarProduto,
        removerProduto,
        atualizarQuantidade,
        atualizarCarrinho,
        limparCarrinho,
      }}
    >
      {children}
    </CarrinhoContext.Provider>
  );
};
