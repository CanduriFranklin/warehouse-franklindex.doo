import React from 'react';
import { Link } from 'react-router-dom';
import { FiShoppingCart, FiTrash2 } from 'react-icons/fi';
import { Card, Button, Loading } from '../components/common';
import { useCarrinho } from '../context/CarrinhoContext';
import { formatarDinheiro } from '../utils/formatters';

export const CarrinhoPage: React.FC = () => {
  const { carrinho, loading, removerProduto, atualizarQuantidade } = useCarrinho();
  
  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Loading size="lg" fullScreen />
      </div>
    );
  }
  
  if (!carrinho || carrinho.itens.length === 0) {
    return (
      <Card className="text-center py-12">
        <FiShoppingCart className="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <h2 className="text-2xl font-bold mb-2">Seu carrinho está vazio</h2>
        <p className="text-gray-600 mb-6">Adicione produtos para começar suas compras</p>
        <Link to="/produtos">
          <Button>Ver Produtos</Button>
        </Link>
      </Card>
    );
  }
  
  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Meu Carrinho</h1>
      
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Lista de itens */}
        <div className="lg:col-span-2 space-y-4">
          {carrinho.itens.map((item) => (
            <Card key={item.id}>
              <div className="flex gap-4">
                <div className="w-24 h-24 bg-gray-100 rounded-lg flex items-center justify-center flex-shrink-0">
                  <FiShoppingCart className="w-12 h-12 text-gray-400" />
                </div>
                
                <div className="flex-1">
                  <h3 className="text-lg font-bold mb-1">{item.produto.nome}</h3>
                  <p className="text-sm text-gray-600 mb-2">
                    {formatarDinheiro(item.precoUnitario)} x {item.quantidade}
                  </p>
                  
                  <div className="flex items-center gap-4">
                    <div className="flex items-center gap-2">
                      <Button
                        size="sm"
                        variant="secondary"
                        onClick={() => atualizarQuantidade(item.produto.id, item.quantidade - 1)}
                        disabled={item.quantidade <= 1}
                      >
                        -
                      </Button>
                      <span className="w-12 text-center font-medium">{item.quantidade}</span>
                      <Button
                        size="sm"
                        variant="secondary"
                        onClick={() => atualizarQuantidade(item.produto.id, item.quantidade + 1)}
                      >
                        +
                      </Button>
                    </div>
                    
                    <button
                      onClick={() => removerProduto(item.produto.id)}
                      className="text-red-600 hover:text-red-700 flex items-center gap-1"
                    >
                      <FiTrash2 />
                      Remover
                    </button>
                  </div>
                </div>
                
                <div className="text-right">
                  <p className="text-xl font-bold text-primary-600">
                    {formatarDinheiro(item.subtotal)}
                  </p>
                </div>
              </div>
            </Card>
          ))}
        </div>
        
        {/* Resumo */}
        <div>
          <Card>
            <h2 className="text-xl font-bold mb-4">Resumo do Pedido</h2>
            
            <div className="space-y-2 mb-4">
              <div className="flex justify-between">
                <span className="text-gray-600">Subtotal:</span>
                <span className="font-medium">{formatarDinheiro(carrinho.valorTotal)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Frete:</span>
                <span className="font-medium text-green-600">Grátis</span>
              </div>
            </div>
            
            <div className="border-t pt-4 mb-6">
              <div className="flex justify-between items-center">
                <span className="text-lg font-bold">Total:</span>
                <span className="text-2xl font-bold text-primary-600">
                  {formatarDinheiro(carrinho.valorTotal)}
                </span>
              </div>
            </div>
            
            <Link to="/checkout">
              <Button className="w-full">Finalizar Compra</Button>
            </Link>
          </Card>
        </div>
      </div>
    </div>
  );
};
