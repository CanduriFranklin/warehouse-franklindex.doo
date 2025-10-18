import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCarrinho } from '../context/CarrinhoContext';
import { useAuth } from '../context/AuthContext';
import type { Produto } from '../types';

interface ProdutoCardProps {
  produto: Produto;
}

export default function ProdutoCard({ produto }: ProdutoCardProps) {
  const [quantidade, setQuantidade] = useState(1);
  const [loading, setLoading] = useState(false);
  const [added, setAdded] = useState(false);
  const { adicionarItem } = useCarrinho();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const adicionarAoCarrinho = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    setLoading(true);
    try {
      await adicionarItem(produto.id, quantidade);
      setAdded(true);
      setTimeout(() => setAdded(false), 2000);
      setQuantidade(1);
    } catch (error) {
      console.error('Erro ao adicionar ao carrinho:', error);
    } finally {
      setLoading(false);
    }
  };

  const incrementar = () => {
    if (quantidade < produto.quantidadeEstoque) {
      setQuantidade(quantidade + 1);
    }
  };

  const decrementar = () => {
    if (quantidade > 1) {
      setQuantidade(quantidade - 1);
    }
  };

  return (
    <div className="group bg-white rounded-xl shadow-md overflow-hidden hover:shadow-2xl transform hover:-translate-y-1 transition-all duration-300">
      {/* Badge de status */}
      {added && (
        <div className="absolute top-4 right-4 z-10 bg-green-500 text-white px-3 py-1 rounded-full text-sm font-medium animate-bounce">
          ✓ Adicionado!
        </div>
      )}
      
      {/* Imagem do produto */}
      <div className="relative h-56 bg-gradient-to-br from-indigo-100 via-purple-50 to-pink-100 flex items-center justify-center overflow-hidden">
        <span className="text-7xl group-hover:scale-110 transition-transform duration-300">📦</span>
        {produto.quantidadeEstoque === 0 && (
          <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <span className="text-white font-bold text-lg">ESGOTADO</span>
          </div>
        )}
      </div>
      
      {/* Conteúdo */}
      <div className="p-5">
        {/* Categoria */}
        <span className="inline-block px-3 py-1 text-xs font-bold text-indigo-700 bg-indigo-100 rounded-full uppercase tracking-wide">
          {produto.categoria}
        </span>
        
        {/* Nome do produto */}
        <h3 className="text-xl font-bold mt-3 line-clamp-2 text-gray-900 group-hover:text-indigo-600 transition-colors">
          {produto.nome}
        </h3>
        
        {/* Descrição */}
        <p className="text-sm text-gray-600 mt-2 line-clamp-2 leading-relaxed">
          {produto.descricao}
        </p>
        
        {/* Estoque */}
        <div className="mt-4 flex items-center gap-2">
          {produto.quantidadeEstoque > 0 ? (
            <>
              <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
              <span className="text-sm text-green-700 font-semibold">
                {produto.quantidadeEstoque} disponíveis
              </span>
            </>
          ) : (
            <>
              <div className="w-2 h-2 bg-red-500 rounded-full"></div>
              <span className="text-sm text-red-700 font-semibold">
                Indisponível
              </span>
            </>
          )}
        </div>
        
        {/* Preço */}
        <div className="mt-5 mb-4">
          <div className="flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-gray-900">
              R$ {produto.preco.valor.toFixed(2)}
            </span>
            <span className="text-sm text-gray-500">/ unidade</span>
          </div>
        </div>
        
        {/* Controles de quantidade e botão */}
        <div className="space-y-3">
          {/* Quantidade */}
          <div className="flex items-center justify-center gap-3 bg-gray-50 rounded-lg p-2">
            <button
              onClick={decrementar}
              disabled={quantidade <= 1 || loading}
              className="w-10 h-10 flex items-center justify-center bg-white rounded-lg border-2 border-gray-300 text-gray-700 font-bold hover:bg-indigo-50 hover:border-indigo-500 hover:text-indigo-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-sm"
            >
              −
            </button>
            <span className="w-12 text-center text-xl font-bold text-gray-900">
              {quantidade}
            </span>
            <button
              onClick={incrementar}
              disabled={quantidade >= produto.quantidadeEstoque || loading}
              className="w-10 h-10 flex items-center justify-center bg-white rounded-lg border-2 border-gray-300 text-gray-700 font-bold hover:bg-indigo-50 hover:border-indigo-500 hover:text-indigo-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-sm"
            >
              +
            </button>
          </div>
          
          {/* Botão adicionar */}
          <button
            onClick={adicionarAoCarrinho}
            disabled={loading || produto.quantidadeEstoque === 0 || !produto.ativo}
            className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-3 rounded-lg font-bold text-base hover:from-indigo-700 hover:to-purple-700 disabled:from-gray-400 disabled:to-gray-400 disabled:cursor-not-allowed transform hover:scale-105 active:scale-95 transition-all duration-200 shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
          >
            {loading ? (
              <>
                <svg className="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span>Adicionando...</span>
              </>
            ) : (
              <>
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <span>{isAuthenticated ? 'Adicionar ao Carrinho' : 'Fazer Login'}</span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
