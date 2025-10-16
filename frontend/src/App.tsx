import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { CarrinhoProvider } from './context/CarrinhoContext';
import { Layout } from './components/layout';
import { HomePage } from './pages/HomePage';
import { ProdutosPage } from './pages/ProdutosPage';
import { CarrinhoPage } from './pages/CarrinhoPage';

function App() {
  return (
    <Router>
      <CarrinhoProvider>
        <Layout>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/produtos" element={<ProdutosPage />} />
            <Route path="/carrinho" element={<CarrinhoPage />} />
            <Route path="*" element={
              <div className="text-center py-20">
                <h1 className="text-4xl font-bold mb-4">404 - Página Não Encontrada</h1>
                <p className="text-gray-600 mb-6">A página que você está procurando não existe</p>
                <a href="/" className="text-primary-600 hover:underline">Voltar para Home</a>
              </div>
            } />
          </Routes>
        </Layout>
      </CarrinhoProvider>
    </Router>
  );
}

export default App;
