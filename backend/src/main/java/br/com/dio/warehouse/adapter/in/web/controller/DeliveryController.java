package br.com.dio.warehouse.adapter.in.web.controller;

import br.com.dio.warehouse.adapter.in.web.dto.ReceiveDeliveryRequest;
import br.com.dio.warehouse.adapter.in.web.dto.DeliveryResponse;
import br.com.dio.warehouse.adapter.in.web.mapper.WarehouseMapper;
import br.com.dio.warehouse.application.port.in.ReceiveDeliveryUseCase;
import br.com.dio.warehouse.domain.model.DeliveryBox;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller para gerenciamento de entregas
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "APIs para gerenciamento de entregas de cestas básicas")
public class DeliveryController {

    private final ReceiveDeliveryUseCase receiveDeliveryUseCase;
    private final WarehouseMapper mapper;

    @PostMapping
    @Operation(summary = "Registrar nova entrega", description = "Registra o recebimento de uma nova entrega de cestas básicas no estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrega registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada")
    })
    public ResponseEntity<DeliveryResponse> receiveDelivery(
            @Valid @RequestBody ReceiveDeliveryRequest request) {
        log.info("Receiving new delivery: quantity={}, validationDate={}",
                request.totalQuantity(), request.validationDate());

        ReceiveDeliveryUseCase.ReceiveDeliveryCommand command = mapper.toCommand(request);
        DeliveryBox deliveryBox = receiveDeliveryUseCase.execute(command);
        DeliveryResponse response = mapper.toDeliveryResponse(deliveryBox);

        log.info("Delivery registered successfully: id={}", response.deliveryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
