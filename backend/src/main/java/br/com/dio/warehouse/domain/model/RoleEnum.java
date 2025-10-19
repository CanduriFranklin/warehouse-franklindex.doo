package br.com.dio.warehouse.domain.model;

/**
 * Enumeração de papéis (roles) do sistema.
 * 
 * Define os tipos de usuários e seus respectivos níveis de acesso:
 * - ADMIN: Acesso total ao sistema
 * - WAREHOUSE_MANAGER: Gerenciador de estoque e armazém
 * - SALES: Gerenciador de vendas
 * - CUSTOMER: Cliente/Consumidor
 */
public enum RoleEnum {
    /**
     * Administrador - Acesso total ao sistema.
     * Pode gerenciar usuários, roles, configurações e todas as operações.
     */
    ADMIN("Administrador", "Acesso total ao sistema"),
    
    /**
     * Gerenciador de Armazém - Gerencia estoque e produtos.
     * Pode gerenciar produtos, estoque, entregas e relatórios de warehouse.
     */
    WAREHOUSE_MANAGER("Gerenciador de Armazém", "Gerencia estoque e armazém"),
    
    /**
     * Vendedor - Gerencia vendas e pedidos.
     * Pode processar pedidos, visualizar vendas, clientes e relatórios de vendas.
     */
    SALES("Vendedor", "Gerencia vendas e pedidos"),
    
    /**
     * Cliente - Consumidor padrão.
     * Pode realizar compras, visualizar pedidos e gerenciar sua conta pessoal.
     */
    CUSTOMER("Cliente", "Cliente/Consumidor do sistema");
    
    private final String label;
    private final String description;
    
    /**
     * Construtor privado do enum.
     * 
     * @param label Nome legível da role
     * @param description Descrição da role
     */
    RoleEnum(String label, String description) {
        this.label = label;
        this.description = description;
    }
    
    /**
     * Obtém o rótulo legível da role.
     * 
     * @return String com o nome da role
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Obtém a descrição da role.
     * 
     * @return String com a descrição
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Converte uma string para RoleEnum.
     * Busca case-insensitively.
     * 
     * @param value String do nome da role
     * @return RoleEnum correspondente
     * @throws IllegalArgumentException se a role não existir
     */
    public static RoleEnum fromValue(String value) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role não encontrada: " + value);
    }
}
