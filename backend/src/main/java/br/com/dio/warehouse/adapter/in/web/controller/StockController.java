package br.com.dio.warehouse.adapter.in.web.controller;

import br.com.dio.warehouse.adapter.in.web.dto.StockInfoResponse;
import br.com.dio.warehouse.adapter.in.web.mapper.WarehouseMapper;
import br.com.dio.warehouse.application.port.in.CheckStockUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller para consulta de estoque
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "APIs para consulta de informações de estoque")
public class StockController {

    private final CheckStockUseCase checkStockUseCase;
    private final WarehouseMapper mapper;

    @GetMapping
    @Operation(summary = "Consultar estoque", description = "Retorna informações detalhadas sobre o estado atual do estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do estoque retornadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<StockInfoResponse> getStockInfo() {
        log.debug("Getting stock information");

        CheckStockUseCase.StockInfo stockInfo = checkStockUseCase.execute();
        StockInfoResponse response = mapper.toResponse(stockInfo);

        log.debug("Stock info retrieved: total={}, available={}",
                response.totalBaskets(), response.availableBaskets());
        return ResponseEntity.ok(response);
    }
}
