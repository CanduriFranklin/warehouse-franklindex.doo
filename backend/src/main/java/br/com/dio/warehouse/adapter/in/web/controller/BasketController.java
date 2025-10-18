package br.com.dio.warehouse.adapter.in.web.controller;

import br.com.dio.warehouse.adapter.in.web.dto.SellBasketsRequest;
import br.com.dio.warehouse.adapter.in.web.dto.SellBasketsResponse;
import br.com.dio.warehouse.adapter.in.web.dto.DisposeExpiredBasketsResponse;
import br.com.dio.warehouse.adapter.in.web.mapper.WarehouseMapper;
import br.com.dio.warehouse.application.port.in.SellBasketsUseCase;
import br.com.dio.warehouse.application.port.in.DisposeExpiredBasketsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller para operações com cestas básicas
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
@Tag(name = "Baskets", description = "APIs para operações com cestas básicas")
public class BasketController {

    private final SellBasketsUseCase sellBasketsUseCase;
    private final DisposeExpiredBasketsUseCase disposeExpiredBasketsUseCase;
    private final WarehouseMapper mapper;

    @GetMapping
    @Operation(summary = "Listar cestas", description = "Lista todas as cestas disponíveis no estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> listBaskets() {
        log.info("Listing all baskets");
        return ResponseEntity.ok("{\"message\":\"Basket listing endpoint - implementation pending\"}");
    }

    @PostMapping("/sell")
    @Operation(summary = "Vender cestas", description = "Registra a venda de cestas básicas disponíveis no estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    })
    public ResponseEntity<SellBasketsResponse> sellBaskets(
            @Valid @RequestBody SellBasketsRequest request) {
        log.info("Processing basket sale: quantity={}", request.quantity());

        SellBasketsUseCase.SellBasketsCommand command = mapper.toCommand(request);
        SellBasketsUseCase.SellBasketsResult result = sellBasketsUseCase.execute(command);
        SellBasketsResponse response = mapper.toResponse(result);

        log.info("Baskets sold successfully: quantity={}", response.totalSold());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/dispose-expired")
    @Operation(summary = "Descartar cestas vencidas", description = "Identifica e descarta automaticamente todas as cestas vencidas do estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Descarte realizado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<DisposeExpiredBasketsResponse> disposeExpiredBaskets() {
        log.info("Processing disposal of expired baskets");

        DisposeExpiredBasketsUseCase.DisposeExpiredBasketsResult result =
                disposeExpiredBasketsUseCase.execute();
        DisposeExpiredBasketsResponse response = mapper.toResponse(result);

        log.info("Expired baskets disposed: quantity={}", response.totalDisposed());
        return ResponseEntity.ok(response);
    }
}
