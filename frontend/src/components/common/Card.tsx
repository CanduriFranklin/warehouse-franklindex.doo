import React, { type ReactNode } from 'react';

interface CardProps {
  children: ReactNode;
  className?: string;
  hover?: boolean;
}

export const Card: React.FC<CardProps> = ({ children, className = '', hover = false }) => {
  const hoverClass = hover ? 'hover:shadow-lg cursor-pointer' : '';
  
  return (
    <div className={`bg-white rounded-lg shadow-md p-6 transition-shadow ${hoverClass} ${className}`}>
      {children}
    </div>
  );
};
