import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { produtoService } from '../services/produtoService';
import ProdutoCard from '../components/ProdutoCard';
import { useAuth } from '../context/AuthContext';
import type { Produto } from '../types';

export default function HomePage() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  const { isAuthenticated, user } = useAuth();

  useEffect(() => {
    carregarProdutos();
  }, []);

  const carregarProdutos = async () => {
    try {
      const response = await produtoService.listarTodos(0, 6);
      setProdutos(response.content || []); // Top 6 produtos
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto"></div>
          <p className="text-xl text-gray-600 mt-4">Carregando...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <div className="container mx-auto px-4 py-16">
          <div className="max-w-3xl">
            <h1 className="text-5xl font-bold mb-4">
              Bem-vindo à Warehouse! 🛒
            </h1>
            <p className="text-xl mb-8 opacity-90">
              A melhor loja online para encontrar produtos de qualidade com os melhores preços.
            </p>
            {isAuthenticated ? (
              <div className="flex flex-col sm:flex-row gap-4">
                <Link
                  to="/produtos"
                  className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition-colors text-center"
                >
                  Ver Todos os Produtos
                </Link>
                <p className="text-lg self-center">
                  Olá, <strong>{user?.username}</strong>! 👋
                </p>
              </div>
            ) : (
              <div className="flex flex-col sm:flex-row gap-4">
                <Link
                  to="/login"
                  className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition-colors text-center"
                >
                  Fazer Login
                </Link>
                <Link
                  to="/produtos"
                  className="bg-blue-700 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-800 transition-colors text-center"
                >
                  Ver Produtos
                </Link>
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="container mx-auto px-4 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-lg shadow-md text-center">
            <div className="text-5xl mb-4">📦</div>
            <h3 className="text-xl font-semibold mb-2">Produtos de Qualidade</h3>
            <p className="text-gray-600">
              Selecionamos os melhores produtos para você
            </p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-md text-center">
            <div className="text-5xl mb-4">🚚</div>
            <h3 className="text-xl font-semibold mb-2">Entrega Rápida</h3>
            <p className="text-gray-600">
              Receba seus produtos no conforto da sua casa
            </p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-md text-center">
            <div className="text-5xl mb-4">💳</div>
            <h3 className="text-xl font-semibold mb-2">Pagamento Seguro</h3>
            <p className="text-gray-600">
              Suas transações estão protegidas
            </p>
          </div>
        </div>
      </section>

      {/* Produtos em Destaque */}
      <section className="container mx-auto px-4 py-12">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-3xl font-bold text-gray-800">
            Produtos em Destaque
          </h2>
          <Link
            to="/produtos"
            className="text-blue-600 hover:text-blue-800 font-medium"
          >
            Ver Todos →
          </Link>
        </div>

        {produtos.length === 0 ? (
          <div className="text-center py-12 bg-white rounded-lg shadow">
            <p className="text-xl text-gray-600">
              Nenhum produto disponível no momento.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {produtos.map((produto) => (
              <ProdutoCard key={produto.id} produto={produto} />
            ))}
          </div>
        )}
      </section>

      {/* CTA Section */}
      {!isAuthenticated && (
        <section className="bg-blue-600 text-white py-16">
          <div className="container mx-auto px-4 text-center">
            <h2 className="text-3xl font-bold mb-4">
              Pronto para começar a comprar?
            </h2>
            <p className="text-xl mb-8 opacity-90">
              Faça login ou cadastre-se para ter acesso a todas as funcionalidades
            </p>
            <Link
              to="/login"
              className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition-colors inline-block"
            >
              Fazer Login Agora
            </Link>
          </div>
        </section>
      )}
    </div>
  );
}
