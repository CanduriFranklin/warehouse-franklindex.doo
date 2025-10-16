package br.com.dio.storefront.infrastructure.web.exception;

import br.com.dio.storefront.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler para controllers REST.
 * Converte domain exceptions em respostas HTTP apropriadas usando RFC 7807 (Problem Details).
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ProblemDetail handleProdutoNaoEncontrado(ProdutoNaoEncontradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, 
                ex.getMessage()
        );
        problem.setTitle("Produto não encontrado");
        problem.setType(URI.create("https://api.storefront.com/errors/produto-nao-encontrado"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ProblemDetail handleClienteNaoEncontrado(ClienteNaoEncontradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, 
                ex.getMessage()
        );
        problem.setTitle("Cliente não encontrado");
        problem.setType(URI.create("https://api.storefront.com/errors/cliente-nao-encontrado"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    @ExceptionHandler(CarrinhoNaoEncontradoException.class)
    public ProblemDetail handleCarrinhoNaoEncontrado(CarrinhoNaoEncontradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, 
                ex.getMessage()
        );
        problem.setTitle("Carrinho não encontrado");
        problem.setType(URI.create("https://api.storefront.com/errors/carrinho-nao-encontrado"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ProblemDetail handlePedidoNaoEncontrado(PedidoNaoEncontradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, 
                ex.getMessage()
        );
        problem.setTitle("Pedido não encontrado");
        problem.setType(URI.create("https://api.storefront.com/errors/pedido-nao-encontrado"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ProblemDetail handleEstoqueInsuficiente(EstoqueInsuficienteException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, 
                ex.getMessage()
        );
        problem.setTitle("Estoque insuficiente");
        problem.setType(URI.create("https://api.storefront.com/errors/estoque-insuficiente"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    @ExceptionHandler(CarrinhoInvalidoException.class)
    public ProblemDetail handleCarrinhoInvalido(CarrinhoInvalidoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, 
                ex.getMessage()
        );
        problem.setTitle("Carrinho inválido");
        problem.setType(URI.create("https://api.storefront.com/errors/carrinho-invalido"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    @ExceptionHandler(PedidoInvalidoException.class)
    public ProblemDetail handlePedidoInvalido(PedidoInvalidoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, 
                ex.getMessage()
        );
        problem.setTitle("Pedido inválido");
        problem.setType(URI.create("https://api.storefront.com/errors/pedido-invalido"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    @ExceptionHandler(StorefrontDomainException.class)
    public ProblemDetail handleStorefrontDomainException(StorefrontDomainException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, 
                ex.getMessage()
        );
        problem.setTitle("Erro de domínio");
        problem.setType(URI.create("https://api.storefront.com/errors/domain-error"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Erro de validação nos campos enviados"
        );
        problem.setTitle("Erro de validação");
        problem.setType(URI.create("https://api.storefront.com/errors/validation-error"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errors", errors);
        return problem;
    }
    
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor: " + ex.getMessage()
        );
        problem.setTitle("Erro interno");
        problem.setType(URI.create("https://api.storefront.com/errors/internal-error"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
