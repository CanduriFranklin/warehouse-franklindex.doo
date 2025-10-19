import { useContext } from 'react';
import { AuthContext, type AuthContextType } from '../context/AuthContext';

/**
 * Hook customizado para acessar o contexto de autenticação.
 * 
 * @returns AuthContextType com user, token, login, register, logout, etc
 * @throws Error se usado fora de AuthProvider
 */
export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider');
  }
  return context;
}
