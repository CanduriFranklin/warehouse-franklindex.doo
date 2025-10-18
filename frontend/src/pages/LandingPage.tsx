import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LandingPage() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900">
      {/* Hero Section */}
      <div className="relative overflow-hidden">
        {/* Animated Background */}
        <div className="absolute inset-0">
          <div className="absolute top-20 left-20 w-72 h-72 bg-purple-500 rounded-full mix-blend-multiply filter blur-xl opacity-20 animate-blob"></div>
          <div className="absolute top-40 right-20 w-72 h-72 bg-yellow-500 rounded-full mix-blend-multiply filter blur-xl opacity-20 animate-blob animation-delay-2000"></div>
          <div className="absolute -bottom-8 left-1/2 w-72 h-72 bg-pink-500 rounded-full mix-blend-multiply filter blur-xl opacity-20 animate-blob animation-delay-4000"></div>
        </div>

        {/* Content */}
        <div className="relative container mx-auto px-6 pt-32 pb-24">
          <div className="max-w-4xl mx-auto text-center">
            {/* Badge */}
            <div className="inline-flex items-center gap-2 bg-white bg-opacity-10 backdrop-blur-lg rounded-full px-6 py-2 mb-8 border border-white border-opacity-20">
              <span className="relative flex h-3 w-3">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-3 w-3 bg-green-500"></span>
              </span>
              <span className="text-white text-sm font-medium">Sistema Online</span>
            </div>

            {/* Title */}
            <h1 className="text-6xl md:text-7xl font-extrabold text-white mb-6 leading-tight">
              Gerencie seu
              <span className="block bg-gradient-to-r from-purple-400 via-pink-400 to-red-400 text-transparent bg-clip-text">
                Warehouse com Eficiência
              </span>
            </h1>

            {/* Subtitle */}
            <p className="text-xl md:text-2xl text-gray-300 mb-12 leading-relaxed max-w-3xl mx-auto">
              Plataforma completa para gerenciamento de estoque, produtos e pedidos.
              Moderna, rápida e intuitiva.
            </p>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
              {isAuthenticated ? (
                <Link
                  to="/dashboard"
                  className="group relative px-8 py-4 bg-gradient-to-r from-purple-600 to-pink-600 text-white font-bold text-lg rounded-xl overflow-hidden transform hover:scale-105 transition-all duration-300 shadow-2xl hover:shadow-purple-500/50"
                >
                  <span className="relative z-10 flex items-center gap-2">
                    Ir para Dashboard
                    <svg className="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7l5 5m0 0l-5 5m5-5H6" />
                    </svg>
                  </span>
                  <div className="absolute inset-0 bg-gradient-to-r from-pink-600 to-purple-600 opacity-0 group-hover:opacity-100 transition-opacity"></div>
                </Link>
              ) : (
                <>
                  <Link
                    to="/auth"
                    className="group relative px-8 py-4 bg-gradient-to-r from-purple-600 to-pink-600 text-white font-bold text-lg rounded-xl overflow-hidden transform hover:scale-105 transition-all duration-300 shadow-2xl hover:shadow-purple-500/50"
                  >
                    <span className="relative z-10 flex items-center gap-2">
                      Começar Agora
                      <svg className="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7l5 5m0 0l-5 5m5-5H6" />
                      </svg>
                    </span>
                    <div className="absolute inset-0 bg-gradient-to-r from-pink-600 to-purple-600 opacity-0 group-hover:opacity-100 transition-opacity"></div>
                  </Link>

                  <a
                    href="#features"
                    className="px-8 py-4 bg-white bg-opacity-10 backdrop-blur-lg text-white font-bold text-lg rounded-xl border-2 border-white border-opacity-20 hover:bg-opacity-20 transition-all duration-300"
                  >
                    Saiba Mais
                  </a>
                </>
              )}
            </div>

            {/* Stats */}
            <div className="mt-20 grid grid-cols-1 md:grid-cols-3 gap-8">
              {[
                { label: 'Produtos', value: '1000+', icon: '📦' },
                { label: 'Clientes', value: '500+', icon: '👥' },
                { label: 'Pedidos/mês', value: '5000+', icon: '📊' },
              ].map((stat, i) => (
                <div
                  key={i}
                  className="bg-white bg-opacity-10 backdrop-blur-lg rounded-2xl p-6 border border-white border-opacity-20 hover:bg-opacity-20 transition-all duration-300 transform hover:-translate-y-2"
                >
                  <div className="text-4xl mb-2">{stat.icon}</div>
                  <div className="text-3xl font-bold text-white mb-1">{stat.value}</div>
                  <div className="text-gray-300">{stat.label}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div id="features" className="py-24 bg-white">
        <div className="container mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-5xl font-extrabold text-gray-900 mb-4">
              Recursos Poderosos
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Tudo que você precisa para gerenciar seu negócio em um só lugar
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {[
              {
                icon: '🎯',
                title: 'Gestão Inteligente',
                description: 'Controle total sobre seu estoque com dashboards em tempo real'
              },
              {
                icon: '📱',
                title: 'Interface Moderna',
                description: 'Design intuitivo e responsivo para qualquer dispositivo'
              },
              {
                icon: '⚡',
                title: 'Performance',
                description: 'Sistema rápido e confiável com tecnologia de ponta'
              },
              {
                icon: '🔒',
                title: 'Segurança',
                description: 'Seus dados protegidos com criptografia avançada'
              },
              {
                icon: '📊',
                title: 'Relatórios',
                description: 'Análises detalhadas para decisões estratégicas'
              },
              {
                icon: '🚀',
                title: 'Escalável',
                description: 'Cresce junto com seu negócio sem limitações'
              },
            ].map((feature, i) => (
              <div
                key={i}
                className="group bg-gradient-to-br from-gray-50 to-white rounded-2xl p-8 border-2 border-gray-100 hover:border-purple-300 hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2"
              >
                <div className="text-5xl mb-4 group-hover:scale-110 transition-transform">
                  {feature.icon}
                </div>
                <h3 className="text-2xl font-bold text-gray-900 mb-3">
                  {feature.title}
                </h3>
                <p className="text-gray-600 leading-relaxed">
                  {feature.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* CTA Section */}
      {!isAuthenticated && (
        <div className="py-24 bg-gradient-to-r from-purple-600 to-pink-600">
          <div className="container mx-auto px-6 text-center">
            <h2 className="text-4xl md:text-5xl font-extrabold text-white mb-6">
              Pronto para começar?
            </h2>
            <p className="text-xl text-purple-100 mb-10 max-w-2xl mx-auto">
              Crie sua conta gratuitamente e comece a transformar seu negócio hoje mesmo
            </p>
            <Link
              to="/auth"
              className="inline-block px-10 py-5 bg-white text-purple-600 font-bold text-xl rounded-xl hover:shadow-2xl transform hover:scale-105 transition-all duration-300"
            >
              Criar Conta Grátis
            </Link>
          </div>
        </div>
      )}

      {/* Footer */}
      <footer className="bg-slate-900 text-gray-400 py-12">
        <div className="container mx-auto px-6 text-center">
          <p className="text-lg mb-4">© 2025 Warehouse Manager. Todos os direitos reservados.</p>
          <div className="flex justify-center gap-6">
            <a href="#" className="hover:text-white transition-colors">Termos</a>
            <a href="#" className="hover:text-white transition-colors">Privacidade</a>
            <a href="#" className="hover:text-white transition-colors">Suporte</a>
          </div>
        </div>
      </footer>

      <style>{`
        @keyframes blob {
          0%, 100% { transform: translate(0, 0) scale(1); }
          25% { transform: translate(20px, -50px) scale(1.1); }
          50% { transform: translate(-20px, 20px) scale(0.9); }
          75% { transform: translate(50px, 50px) scale(1.05); }
        }
        .animate-blob {
          animation: blob 7s infinite;
        }
        .animation-delay-2000 {
          animation-delay: 2s;
        }
        .animation-delay-4000 {
          animation-delay: 4s;
        }
      `}</style>
    </div>
  );
}
