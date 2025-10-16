import React from 'react';
import { Link } from 'react-router-dom';
import { FiShoppingCart, FiUser, FiPackage } from 'react-icons/fi';
import { useCarrinho } from '../../context/CarrinhoContext';

export const Header: React.FC = () => {
  const { carrinho } = useCarrinho();
  const quantidadeItens = carrinho?.quantidadeTotal || 0;
  
  return (
    <header className="bg-white shadow-md sticky top-0 z-40">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 text-2xl font-bold text-primary-600">
            <FiPackage className="w-8 h-8" />
            <span>Storefront</span>
          </Link>
          
          {/* Navegação */}
          <nav className="hidden md:flex items-center gap-6">
            <Link to="/" className="text-gray-700 hover:text-primary-600 transition-colors font-medium">
              Início
            </Link>
            <Link to="/produtos" className="text-gray-700 hover:text-primary-600 transition-colors font-medium">
              Produtos
            </Link>
            <Link to="/sobre" className="text-gray-700 hover:text-primary-600 transition-colors font-medium">
              Sobre
            </Link>
          </nav>
          
          {/* Ações */}
          <div className="flex items-center gap-4">
            <Link
              to="/pedidos"
              className="flex items-center gap-2 text-gray-700 hover:text-primary-600 transition-colors"
              title="Meus Pedidos"
            >
              <FiUser className="w-6 h-6" />
              <span className="hidden sm:inline">Pedidos</span>
            </Link>
            
            <Link
              to="/carrinho"
              className="relative flex items-center gap-2 text-gray-700 hover:text-primary-600 transition-colors"
              title="Carrinho"
            >
              <FiShoppingCart className="w-6 h-6" />
              <span className="hidden sm:inline">Carrinho</span>
              {quantidadeItens > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
                  {quantidadeItens}
                </span>
              )}
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
};
