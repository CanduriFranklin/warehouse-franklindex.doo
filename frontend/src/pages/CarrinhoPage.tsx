import { useCarrinho } from '../context/CarrinhoContext';
import { useNavigate } from 'react-router-dom';

export default function CarrinhoPage() {
  const { itens, total, loading, removerItem, atualizarQuantidade } = useCarrinho();
  const navigate = useNavigate();

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto"></div>
          <p className="text-xl text-gray-600 mt-4">Carregando carrinho...</p>
        </div>
      </div>
    );
  }

  if (itens.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="container mx-auto px-4 py-12">
          <div className="max-w-2xl mx-auto text-center bg-white rounded-lg shadow-lg p-12">
            <div className="text-8xl mb-6">🛒</div>
            <h1 className="text-3xl font-bold text-gray-800 mb-4">
              Seu carrinho está vazio
            </h1>
            <p className="text-gray-600 mb-8 text-lg">
              Adicione produtos para começar suas compras!
            </p>
            <button
              onClick={() => navigate('/produtos')}
              className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors"
            >
              Ver Produtos
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-4xl font-bold mb-8 text-gray-800">Meu Carrinho</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Lista de Itens */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-lg shadow-lg overflow-hidden">
              {itens.map((item, index) => (
                <div
                  key={item.id}
                  className={`p-6 flex items-center gap-4 ${
                    index !== itens.length - 1 ? 'border-b border-gray-200' : ''
                  }`}
                >
                  {/* Imagem do produto */}
                  <div className="w-20 h-20 bg-gradient-to-br from-blue-100 to-blue-200 rounded flex items-center justify-center flex-shrink-0">
                    <span className="text-3xl">📦</span>
                  </div>

                  {/* Informações do produto */}
                  <div className="flex-1">
                    <h3 className="font-semibold text-lg text-gray-900">
                      {item.produtoNome}
                    </h3>
                    <p className="text-gray-600">
                      R$ {item.produtoPreco.toFixed(2)} cada
                    </p>
                  </div>

                  {/* Controles de quantidade */}
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => atualizarQuantidade(item.produtoId, item.quantidade - 1)}
                      disabled={item.quantidade <= 1}
                      className="w-8 h-8 border border-gray-300 rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed font-semibold"
                    >
                      -
                    </button>
                    <span className="w-12 text-center font-semibold">
                      {item.quantidade}
                    </span>
                    <button
                      onClick={() => atualizarQuantidade(item.produtoId, item.quantidade + 1)}
                      className="w-8 h-8 border border-gray-300 rounded hover:bg-gray-100 font-semibold"
                    >
                      +
                    </button>
                  </div>

                  {/* Subtotal */}
                  <div className="text-right w-24">
                    <p className="font-bold text-lg text-gray-900">
                      R$ {item.subtotal.toFixed(2)}
                    </p>
                  </div>

                  {/* Botão remover */}
                  <button
                    onClick={() => removerItem(item.produtoId)}
                    className="text-red-600 hover:text-red-800 p-2"
                    title="Remover item"
                  >
                    <svg
                      className="w-6 h-6"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                      />
                    </svg>
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* Resumo do Pedido */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-lg p-6 sticky top-4">
              <h2 className="text-2xl font-bold mb-4 text-gray-800">
                Resumo do Pedido
              </h2>

              <div className="space-y-3 mb-6">
                <div className="flex justify-between text-gray-600">
                  <span>Subtotal:</span>
                  <span>R$ {total.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Frete:</span>
                  <span className="text-green-600 font-medium">Grátis</span>
                </div>
                <div className="border-t border-gray-200 pt-3">
                  <div className="flex justify-between text-xl font-bold text-gray-900">
                    <span>Total:</span>
                    <span>R$ {total.toFixed(2)}</span>
                  </div>
                </div>
              </div>

              <button
                onClick={() => alert('Checkout em desenvolvimento')}
                className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 transition-colors mb-3"
              >
                Finalizar Compra
              </button>

              <button
                onClick={() => navigate('/produtos')}
                className="w-full border border-gray-300 py-3 rounded-lg font-medium hover:bg-gray-50 transition-colors"
              >
                Continuar Comprando
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
