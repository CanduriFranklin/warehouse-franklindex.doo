import React from 'react';
import { FiGithub, FiMail, FiPhone } from 'react-icons/fi';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-800 text-white mt-auto">
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Sobre */}
          <div>
            <h3 className="text-lg font-bold mb-4">Sobre o Storefront</h3>
            <p className="text-gray-300 text-sm">
              Aplicação de e-commerce moderna desenvolvida com DDD e Hexagonal Architecture.
              Backend em Java/Spring Boot e Frontend em React/TypeScript.
            </p>
          </div>
          
          {/* Links Úteis */}
          <div>
            <h3 className="text-lg font-bold mb-4">Links Úteis</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <a href="/" className="text-gray-300 hover:text-white transition-colors">
                  Início
                </a>
              </li>
              <li>
                <a href="/produtos" className="text-gray-300 hover:text-white transition-colors">
                  Produtos
                </a>
              </li>
              <li>
                <a href="/carrinho" className="text-gray-300 hover:text-white transition-colors">
                  Carrinho
                </a>
              </li>
              <li>
                <a href="/pedidos" className="text-gray-300 hover:text-white transition-colors">
                  Meus Pedidos
                </a>
              </li>
            </ul>
          </div>
          
          {/* Contato */}
          <div>
            <h3 className="text-lg font-bold mb-4">Contato</h3>
            <ul className="space-y-2 text-sm">
              <li className="flex items-center gap-2 text-gray-300">
                <FiMail className="w-4 h-4" />
                <span>contato@storefront.com</span>
              </li>
              <li className="flex items-center gap-2 text-gray-300">
                <FiPhone className="w-4 h-4" />
                <span>(11) 98765-4321</span>
              </li>
              <li className="flex items-center gap-2 text-gray-300">
                <FiGithub className="w-4 h-4" />
                <a
                  href="https://github.com/CanduriFranklin/warehouse-franklindex.doo"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="hover:text-white transition-colors"
                >
                  GitHub
                </a>
              </li>
            </ul>
          </div>
        </div>
        
        <div className="border-t border-gray-700 mt-8 pt-6 text-center text-sm text-gray-400">
          <p>&copy; {new Date().getFullYear()} Storefront - Todos os direitos reservados</p>
          <p className="mt-1">Desenvolvido com ❤️ por Franklin Canduri</p>
        </div>
      </div>
    </footer>
  );
};
