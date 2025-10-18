import ProdutoLista from '../components/ProdutoLista';

export default function ProdutosPage() {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">
            Nossos Produtos
          </h1>
          <p className="text-gray-600">
            Explore nosso catálogo completo de produtos de qualidade
          </p>
        </div>

        {/* Lista de Produtos */}
        <ProdutoLista />
      </div>
    </div>
  );
}
