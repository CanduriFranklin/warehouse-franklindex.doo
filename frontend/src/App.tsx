import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CarrinhoProvider } from './context/CarrinhoContext';
import Navbar from './components/layout/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import LandingPage from './pages/LandingPage';
import AuthPage from './pages/AuthPage';
import DashboardPage from './pages/DashboardPage';
import ProdutosPage from './pages/ProdutosPage';
import CarrinhoPage from './pages/CarrinhoPage';

function App() {
  return (
    <Router>
      <AuthProvider>
        <CarrinhoProvider>
          <div className="min-h-screen bg-gray-50">
            <Navbar />
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<LandingPage />} />
              <Route path="/auth" element={<AuthPage />} />

              {/* Protected Routes */}
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <DashboardPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/produtos"
                element={
                  <ProtectedRoute>
                    <ProdutosPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/carrinho"
                element={
                  <ProtectedRoute>
                    <CarrinhoPage />
                  </ProtectedRoute>
                }
              />

              {/* 404 */}
              <Route
                path="*"
                element={
                  <div className="min-h-screen bg-gradient-to-br from-purple-900 to-pink-900 flex items-center justify-center px-6">
                    <div className="text-center">
                      <h1 className="text-9xl font-extrabold text-white mb-4">404</h1>
                      <h2 className="text-4xl font-bold text-white mb-6">
                        Página Não Encontrada
                      </h2>
                      <p className="text-xl text-purple-200 mb-10">
                        A página que você está procurando não existe
                      </p>
                      <a
                        href="/"
                        className="inline-block px-8 py-4 bg-white text-purple-600 font-bold text-lg rounded-xl hover:shadow-2xl transform hover:scale-105 transition-all"
                      >
                        ← Voltar para Home
                      </a>
                    </div>
                  </div>
                }
              />
            </Routes>
          </div>
        </CarrinhoProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
