import { useEffect, useState } from 'react';
import { produtoService } from '../services/produtoService';
import ProdutoCard from './ProdutoCard';
import type { Produto } from '../types';

export default function ProdutoLista() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [categorias, setCategorias] = useState<string[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    carregarProdutos();
  }, []);

  const carregarProdutos = async () => {
    try {
      setError(null);
      const response = await produtoService.listarTodos(0, 100);
      const produtosData = response.content || [];
      setProdutos(produtosData);
      
      // Extrair categorias únicas
      const cats = [...new Set(produtosData.map((p: Produto) => p.categoria))];
      setCategorias(cats);
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
      setError('Erro ao carregar produtos. Tente novamente mais tarde.');
    } finally {
      setLoading(false);
    }
  };

  const produtosFiltrados = filtroCategoria
    ? produtos.filter(p => p.categoria === filtroCategoria)
    : produtos;

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="text-xl text-gray-600 mt-4">Carregando produtos...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="bg-gradient-to-r from-red-50 to-pink-50 border-2 border-red-200 rounded-xl p-8 shadow-lg">
          <div className="flex items-start gap-4">
            <div className="flex-shrink-0">
              <svg className="w-12 h-12 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <div className="flex-1">
              <h3 className="text-2xl font-bold text-red-800 mb-2">Ops! Algo deu errado</h3>
              <p className="text-red-700 mb-6 text-lg">{error}</p>
              <button
                onClick={carregarProdutos}
                className="bg-gradient-to-r from-red-600 to-pink-600 text-white px-8 py-3 rounded-lg font-bold hover:from-red-700 hover:to-pink-700 transform hover:scale-105 transition-all duration-200 shadow-lg hover:shadow-xl flex items-center gap-2"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
                Tentar Novamente
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div>
      {/* Filtros de Categoria */}
      <div className="mb-8">
        <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>
          Filtrar por Categoria
        </h3>
        <div className="flex flex-wrap gap-3">
          <button
            onClick={() => setFiltroCategoria('')}
            className={`px-6 py-3 rounded-xl font-bold transition-all duration-200 transform hover:scale-105 ${
              !filtroCategoria
                ? 'bg-gradient-to-r from-indigo-600 to-purple-600 text-white shadow-lg'
                : 'bg-white text-gray-700 border-2 border-gray-200 hover:border-indigo-300 shadow-sm'
            }`}
          >
            <span className="flex items-center gap-2">
              Todos
              <span className="bg-white bg-opacity-20 px-2 py-0.5 rounded-full text-sm">
                {produtos.length}
              </span>
            </span>
          </button>
          {categorias.map(cat => (
            <button
              key={cat}
              onClick={() => setFiltroCategoria(cat)}
              className={`px-6 py-3 rounded-xl font-bold transition-all duration-200 transform hover:scale-105 ${
                filtroCategoria === cat
                  ? 'bg-gradient-to-r from-indigo-600 to-purple-600 text-white shadow-lg'
                  : 'bg-white text-gray-700 border-2 border-gray-200 hover:border-indigo-300 shadow-sm'
              }`}
            >
              <span className="flex items-center gap-2">
                {cat}
                <span className={`px-2 py-0.5 rounded-full text-sm ${
                  filtroCategoria === cat 
                    ? 'bg-white bg-opacity-20' 
                    : 'bg-gray-100'
                }`}>
                  {produtos.filter(p => p.categoria === cat).length}
                </span>
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* Grid de Produtos */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {produtosFiltrados.map(produto => (
          <ProdutoCard key={produto.id} produto={produto} />
        ))}
      </div>

      {/* Mensagem quando não há produtos */}
      {produtosFiltrados.length === 0 && (
        <div className="text-center py-12">
          <p className="text-2xl text-gray-600">📦</p>
          <p className="text-lg text-gray-600 mt-2">
            Nenhum produto encontrado nesta categoria.
          </p>
          <button
            onClick={() => setFiltroCategoria('')}
            className="mt-4 bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
          >
            Ver Todos os Produtos
          </button>
        </div>
      )}
    </div>
  );
}
