import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FiShoppingBag, FiTrendingUp, FiPackage } from 'react-icons/fi';
import { Card, Button, Loading } from '../components/common';
import type { Produto } from '../types';
import { produtoService } from '../services/produtoService';
import { formatarDinheiro } from '../utils/formatters';

export const HomePage: React.FC = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    carregarProdutos();
  }, []);
  
  const carregarProdutos = async () => {
    try {
      const response = await produtoService.listarTodos(0, 4);
      setProdutos(response.content);
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="space-y-12">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary-600 to-primary-800 rounded-2xl text-white p-12 text-center">
        <h1 className="text-5xl font-bold mb-4">Bem-vindo ao Storefront</h1>
        <p className="text-xl mb-8 text-primary-100">
          Sua loja online de produtos com arquitetura DDD + Hexagonal
        </p>
        <Link to="/produtos">
          <Button size="lg" variant="secondary">
            <FiShoppingBag className="w-6 h-6" />
            Ver Produtos
          </Button>
        </Link>
      </section>
      
      {/* Features */}
      <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="text-center">
          <FiPackage className="w-12 h-12 text-primary-600 mx-auto mb-4" />
          <h3 className="text-xl font-bold mb-2">Produtos de Qualidade</h3>
          <p className="text-gray-600">
            Oferecemos uma seleção cuidadosa de produtos para você
          </p>
        </Card>
        
        <Card className="text-center">
          <FiTrendingUp className="w-12 h-12 text-primary-600 mx-auto mb-4" />
          <h3 className="text-xl font-bold mb-2">Preços Competitivos</h3>
          <p className="text-gray-600">
            Encontre os melhores preços do mercado aqui
          </p>
        </Card>
        
        <Card className="text-center">
          <FiShoppingBag className="w-12 h-12 text-primary-600 mx-auto mb-4" />
          <h3 className="text-xl font-bold mb-2">Compra Segura</h3>
          <p className="text-gray-600">
            Checkout fácil e seguro para suas compras
          </p>
        </Card>
      </section>
      
      {/* Produtos em Destaque */}
      <section>
        <h2 className="text-3xl font-bold mb-6 text-center">Produtos em Destaque</h2>
        
        {loading ? (
          <div className="flex justify-center py-12">
            <Loading size="lg" />
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {produtos.map((produto) => (
              <Card key={produto.id} hover className="flex flex-col">
                <div className="aspect-square bg-gray-100 rounded-lg mb-4 flex items-center justify-center">
                  {produto.imagemUrl ? (
                    <img
                      src={produto.imagemUrl}
                      alt={produto.nome}
                      className="w-full h-full object-cover rounded-lg"
                    />
                  ) : (
                    <FiPackage className="w-24 h-24 text-gray-400" />
                  )}
                </div>
                
                <div className="flex-1 flex flex-col">
                  <span className="text-xs text-primary-600 font-medium uppercase mb-1">
                    {produto.categoria}
                  </span>
                  <h3 className="text-lg font-bold mb-2 line-clamp-2">
                    {produto.nome}
                  </h3>
                  <p className="text-gray-600 text-sm mb-4 line-clamp-2 flex-1">
                    {produto.descricao}
                  </p>
                  
                  <div className="flex items-center justify-between mt-auto">
                    <span className="text-2xl font-bold text-primary-600">
                      {formatarDinheiro(produto.preco)}
                    </span>
                    <Link to={`/produtos/${produto.id}`}>
                      <Button size="sm">Ver Detalhes</Button>
                    </Link>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}
        
        <div className="text-center mt-8">
          <Link to="/produtos">
            <Button variant="secondary">Ver Todos os Produtos</Button>
          </Link>
        </div>
      </section>
    </div>
  );
};
