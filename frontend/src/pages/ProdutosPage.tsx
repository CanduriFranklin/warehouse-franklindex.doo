import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FiPackage, FiSearch } from 'react-icons/fi';
import { Card, Button, Loading, Input } from '../components/common';
import type { Produto } from '../types';
import { produtoService } from '../services/produtoService';
import { formatarDinheiro } from '../utils/formatters';

export const ProdutosPage: React.FC = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  const [busca, setBusca] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  
  useEffect(() => {
    carregarProdutos();
  }, [page]);
  
  const carregarProdutos = async () => {
    try {
      setLoading(true);
      const response = busca
        ? await produtoService.buscarPorNome(busca, page, 12)
        : await produtoService.listarTodos(page, 12);
      setProdutos(response.content);
      setTotalPages(response.totalPages);
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
    } finally {
      setLoading(false);
    }
  };
  
  const handleBuscar = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    carregarProdutos();
  };
  
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-center gap-4">
        <h1 className="text-3xl font-bold">Nossos Produtos</h1>
        
        <form onSubmit={handleBuscar} className="w-full sm:w-96">
          <div className="relative">
            <Input
              type="text"
              placeholder="Buscar produtos..."
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
              className="pr-10"
            />
            <button
              type="submit"
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-primary-600"
            >
              <FiSearch className="w-5 h-5" />
            </button>
          </div>
        </form>
      </div>
      
      {loading ? (
        <div className="flex justify-center py-20">
          <Loading size="lg" />
        </div>
      ) : produtos.length === 0 ? (
        <Card className="text-center py-12">
          <FiPackage className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-xl font-bold mb-2">Nenhum produto encontrado</h3>
          <p className="text-gray-600">Tente buscar por outro termo</p>
        </Card>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {produtos.map((produto) => (
              <Card key={produto.id} hover className="flex flex-col">
                <div className="aspect-square bg-gray-100 rounded-lg mb-4 flex items-center justify-center overflow-hidden">
                  {produto.imagemUrl ? (
                    <img
                      src={produto.imagemUrl}
                      alt={produto.nome}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <FiPackage className="w-20 h-20 text-gray-400" />
                  )}
                </div>
                
                <div className="flex-1 flex flex-col">
                  <span className="text-xs text-primary-600 font-medium uppercase mb-1">
                    {produto.categoria}
                  </span>
                  <h3 className="text-lg font-bold mb-2 line-clamp-2">
                    {produto.nome}
                  </h3>
                  <p className="text-gray-600 text-sm mb-4 line-clamp-3 flex-1">
                    {produto.descricao}
                  </p>
                  
                  <div className="space-y-3 mt-auto">
                    <div className="flex items-baseline justify-between">
                      <span className="text-2xl font-bold text-primary-600">
                        {formatarDinheiro(produto.preco)}
                      </span>
                      <span className="text-sm text-gray-500">
                        {produto.quantidadeEstoque > 0
                          ? `${produto.quantidadeEstoque} em estoque`
                          : 'Indisponível'}
                      </span>
                    </div>
                    
                    <Link to={`/produtos/${produto.id}`} className="block">
                      <Button className="w-full">Ver Detalhes</Button>
                    </Link>
                  </div>
                </div>
              </Card>
            ))}
          </div>
          
          {/* Paginação */}
          {totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-8">
              <Button
                variant="secondary"
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
              >
                Anterior
              </Button>
              <span className="flex items-center px-4 text-gray-700">
                Página {page + 1} de {totalPages}
              </span>
              <Button
                variant="secondary"
                disabled={page >= totalPages - 1}
                onClick={() => setPage(page + 1)}
              >
                Próxima
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
};
