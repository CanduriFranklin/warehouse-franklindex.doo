import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCarrinho } from '../context/CarrinhoContext';

export default function Header() {
  const { user, isAuthenticated, logout } = useAuth();
  const { itens } = useCarrinho();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const totalItens = itens.reduce((sum, item) => sum + item.quantidade, 0);

  return (
    <header className="bg-blue-600 text-white shadow-lg">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo e Nome */}
          <Link to="/" className="flex items-center space-x-2 hover:opacity-80 transition-opacity">
            <span className="text-3xl">🛒</span>
            <span className="text-2xl font-bold">Warehouse</span>
          </Link>

          {/* Navegação Central */}
          <nav className="hidden md:flex space-x-6">
            <Link
              to="/"
              className="hover:bg-blue-700 px-3 py-2 rounded transition-colors"
            >
              Home
            </Link>
            <Link
              to="/produtos"
              className="hover:bg-blue-700 px-3 py-2 rounded transition-colors"
            >
              Produtos
            </Link>
          </nav>

          {/* Ações (Carrinho e Login/Logout) */}
          <div className="flex items-center space-x-4">
            {/* Carrinho */}
            {isAuthenticated && (
              <Link
                to="/carrinho"
                className="relative hover:bg-blue-700 px-3 py-2 rounded transition-colors flex items-center space-x-1"
              >
                <span className="text-2xl">🛒</span>
                {totalItens > 0 && (
                  <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                    {totalItens}
                  </span>
                )}
              </Link>
            )}

            {/* Login/Logout */}
            {isAuthenticated ? (
              <div className="flex items-center space-x-3">
                <span className="hidden md:inline text-sm">
                  Olá, <strong>{user?.username}</strong>
                </span>
                <button
                  onClick={handleLogout}
                  className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded font-medium transition-colors"
                >
                  Sair
                </button>
              </div>
            ) : (
              <Link
                to="/login"
                className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded font-medium transition-colors"
              >
                Entrar
              </Link>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}
