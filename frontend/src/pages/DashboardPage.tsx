import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCarrinho } from '../context/CarrinhoContext';

export default function DashboardPage() {
  const { user } = useAuth();
  const { itens, total } = useCarrinho();

  const quickActions = [
    {
      title: 'Ver Produtos',
      description: 'Explore nosso catálogo',
      icon: '🛍️',
      link: '/produtos',
      color: 'from-blue-500 to-cyan-500',
    },
    {
      title: 'Meu Carrinho',
      description: `${itens.length} itens`,
      icon: '🛒',
      link: '/carrinho',
      color: 'from-purple-500 to-pink-500',
      badge: itens.length,
    },
    {
      title: 'Pedidos',
      description: 'Histórico de compras',
      icon: '📦',
      link: '/pedidos',
      color: 'from-green-500 to-emerald-500',
    },
    {
      title: 'Perfil',
      description: 'Configurações',
      icon: '⚙️',
      link: '/perfil',
      color: 'from-orange-500 to-red-500',
    },
  ];

  const stats = [
    {
      label: 'Itens no Carrinho',
      value: itens.length,
      icon: '🛒',
      change: '+12%',
      color: 'bg-blue-500',
    },
    {
      label: 'Total do Carrinho',
      value: `R$ ${total.toFixed(2)}`,
      icon: '💰',
      change: '+8%',
      color: 'bg-green-500',
    },
    {
      label: 'Produtos Visitados',
      value: '24',
      icon: '👁️',
      change: '+18%',
      color: 'bg-purple-500',
    },
    {
      label: 'Favoritos',
      value: '8',
      icon: '❤️',
      change: '+5%',
      color: 'bg-pink-500',
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-indigo-600 via-purple-600 to-pink-600 text-white">
        <div className="container mx-auto px-6 py-16">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-4xl md:text-5xl font-extrabold mb-4">
                Bem-vindo de volta, {user?.username || 'Usuário'}! 👋
              </h1>
              <p className="text-xl text-purple-100">
                Veja suas atividades e gerencie seus produtos
              </p>
            </div>
            <div className="hidden md:block">
              <div className="w-32 h-32 bg-white bg-opacity-20 rounded-full flex items-center justify-center backdrop-blur-lg">
                <span className="text-6xl">🎯</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-6 py-12">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
          {stats.map((stat, i) => (
            <div
              key={i}
              className="bg-white rounded-2xl shadow-lg p-6 hover:shadow-2xl transform hover:-translate-y-1 transition-all duration-300"
            >
              <div className="flex items-center justify-between mb-4">
                <div className={`w-14 h-14 ${stat.color} rounded-xl flex items-center justify-center text-2xl shadow-lg`}>
                  {stat.icon}
                </div>
                <span className="text-green-600 font-bold text-sm bg-green-100 px-3 py-1 rounded-full">
                  {stat.change}
                </span>
              </div>
              <div>
                <p className="text-gray-600 text-sm font-medium mb-1">{stat.label}</p>
                <p className="text-3xl font-extrabold text-gray-900">{stat.value}</p>
              </div>
            </div>
          ))}
        </div>

        {/* Quick Actions */}
        <div className="mb-12">
          <h2 className="text-3xl font-extrabold text-gray-900 mb-6 flex items-center gap-3">
            <span className="w-2 h-10 bg-gradient-to-b from-purple-600 to-pink-600 rounded-full"></span>
            Ações Rápidas
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {quickActions.map((action, i) => (
              <Link
                key={i}
                to={action.link}
                className="group relative bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-2xl transform hover:-translate-y-2 transition-all duration-300"
              >
                {action.badge !== undefined && action.badge > 0 && (
                  <div className="absolute top-4 right-4 w-8 h-8 bg-red-500 text-white rounded-full flex items-center justify-center font-bold text-sm shadow-lg animate-pulse">
                    {action.badge}
                  </div>
                )}
                <div className={`h-32 bg-gradient-to-br ${action.color} flex items-center justify-center text-6xl group-hover:scale-110 transition-transform duration-300`}>
                  {action.icon}
                </div>
                <div className="p-6">
                  <h3 className="text-xl font-bold text-gray-900 mb-2 group-hover:text-purple-600 transition-colors">
                    {action.title}
                  </h3>
                  <p className="text-gray-600">{action.description}</p>
                </div>
                <div className="absolute bottom-0 left-0 right-0 h-1 bg-gradient-to-r from-purple-600 to-pink-600 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
              </Link>
            ))}
          </div>
        </div>

        {/* Recent Activity */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Carrinho Preview */}
          <div className="bg-white rounded-2xl shadow-lg p-8">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                <span className="text-3xl">🛒</span>
                Seu Carrinho
              </h3>
              <Link to="/carrinho" className="text-purple-600 font-semibold hover:text-purple-700 transition-colors">
                Ver Todos →
              </Link>
            </div>
            {itens.length > 0 ? (
              <div className="space-y-4">
                {itens.slice(0, 3).map((item) => (
                  <div key={item.id} className="flex items-center gap-4 p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors">
                    <div className="w-16 h-16 bg-gradient-to-br from-purple-200 to-pink-200 rounded-lg flex items-center justify-center text-2xl">
                      📦
                    </div>
                    <div className="flex-1">
                      <p className="font-semibold text-gray-900">{item.produtoNome}</p>
                      <p className="text-sm text-gray-600">Qtd: {item.quantidade}</p>
                    </div>
                    <p className="font-bold text-purple-600">R$ {item.subtotal.toFixed(2)}</p>
                  </div>
                ))}
                {itens.length > 3 && (
                  <p className="text-center text-gray-600 text-sm">
                    + {itens.length - 3} itens a mais
                  </p>
                )}
              </div>
            ) : (
              <div className="text-center py-12">
                <span className="text-6xl mb-4 block">🛒</span>
                <p className="text-gray-600 mb-4">Seu carrinho está vazio</p>
                <Link to="/produtos" className="inline-block px-6 py-3 bg-gradient-to-r from-purple-600 to-pink-600 text-white font-bold rounded-xl hover:shadow-lg transform hover:scale-105 transition-all">
                  Explorar Produtos
                </Link>
              </div>
            )}
          </div>

          {/* Recommendations */}
          <div className="bg-white rounded-2xl shadow-lg p-8">
            <h3 className="text-2xl font-bold text-gray-900 mb-6 flex items-center gap-2">
              <span className="text-3xl">✨</span>
              Recomendados para Você
            </h3>
            <div className="space-y-4">
              {[
                { name: 'Produto Premium A', price: 199.99, rating: 4.8 },
                { name: 'Produto Destaque B', price: 149.99, rating: 4.9 },
                { name: 'Produto Popular C', price: 99.99, rating: 4.7 },
              ].map((product, i) => (
                <div key={i} className="flex items-center gap-4 p-4 bg-gradient-to-r from-purple-50 to-pink-50 rounded-xl hover:shadow-md transition-shadow">
                  <div className="w-16 h-16 bg-gradient-to-br from-indigo-300 to-purple-300 rounded-lg flex items-center justify-center text-2xl">
                    🎁
                  </div>
                  <div className="flex-1">
                    <p className="font-semibold text-gray-900">{product.name}</p>
                    <div className="flex items-center gap-2 mt-1">
                      <span className="text-yellow-500">⭐</span>
                      <span className="text-sm text-gray-600">{product.rating}</span>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-purple-600">R$ {product.price.toFixed(2)}</p>
                    <button className="text-sm text-purple-600 hover:text-purple-700 font-semibold">
                      Ver →
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
