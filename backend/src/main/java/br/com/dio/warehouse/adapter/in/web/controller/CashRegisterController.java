package br.com.dio.warehouse.adapter.in.web.controller;

import br.com.dio.warehouse.adapter.in.web.dto.CashRegisterResponse;
import br.com.dio.warehouse.adapter.in.web.mapper.WarehouseMapper;
import br.com.dio.warehouse.application.port.in.GetCashRegisterUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller para consulta de caixa
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/cash-register")
@RequiredArgsConstructor
@Tag(name = "Cash Register", description = "APIs para consulta de informações financeiras")
public class CashRegisterController {

    private final GetCashRegisterUseCase getCashRegisterUseCase;
    private final WarehouseMapper mapper;

    @GetMapping
    @Operation(summary = "Consultar caixa", description = "Retorna informações financeiras consolidadas do caixa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do caixa retornadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CashRegisterResponse> getCashRegisterInfo() {
        log.debug("Getting cash register information");

        GetCashRegisterUseCase.CashRegisterInfo cashRegisterInfo = getCashRegisterUseCase.execute();
        CashRegisterResponse response = mapper.toResponse(cashRegisterInfo);

        log.debug("Cash register info retrieved: revenue={}, profit={}",
                response.totalRevenue(), response.grossProfit());
        return ResponseEntity.ok(response);
    }
}
