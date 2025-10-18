import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(username, password);
      navigate('/');
    } catch (err) {
      setError('Usuário ou senha incorretos');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100">
      <div className="bg-white p-8 rounded-lg shadow-2xl w-full max-w-md">
        {/* Logo/Título */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-blue-600 mb-2">🛒</h1>
          <h2 className="text-3xl font-bold text-gray-800">Warehouse</h2>
          <p className="text-gray-600 mt-2">Faça login para continuar</p>
        </div>

        {/* Mensagem de Erro */}
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            <p className="font-medium">❌ {error}</p>
          </div>
        )}

        {/* Formulário */}
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-700 font-medium mb-2">
              Usuário
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Digite seu usuário"
              required
              autoFocus
            />
          </div>

          <div className="mb-6">
            <label className="block text-gray-700 font-medium mb-2">
              Senha
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Digite sua senha"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-3 rounded font-semibold hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        {/* Usuários de Teste */}
        <div className="mt-6 text-center text-sm text-gray-600 bg-gray-50 p-4 rounded">
          <p className="font-semibold mb-2">👤 Usuários de teste:</p>
          <ul className="space-y-1">
            <li>
              <strong>admin</strong> / <code className="bg-gray-200 px-1 rounded">Admin@2025!Secure</code>
            </li>
            <li>
              <strong>manager</strong> / <code className="bg-gray-200 px-1 rounded">Manager@2025!Secure</code>
            </li>
            <li>
              <strong>sales</strong> / <code className="bg-gray-200 px-1 rounded">Sales@2025!Secure</code>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}
