package br.com.dio.warehouse.adapter.in.web.mapper;

import br.com.dio.warehouse.adapter.in.web.dto.*;
import br.com.dio.warehouse.application.port.in.*;
import br.com.dio.warehouse.domain.model.DeliveryBox;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between DTOs and domain/application objects
 */
@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    
    // ======== Delivery Mappings ========
    
    /**
     * Maps ReceiveDeliveryRequest to ReceiveDeliveryCommand
     */
    @Mapping(target = "totalQuantity", source = "totalQuantity")
    @Mapping(target = "validationDate", source = "validationDate")
    @Mapping(target = "totalCost", source = "totalCost")
    @Mapping(target = "profitMarginPercentage", source = "profitMarginPercentage")
    ReceiveDeliveryUseCase.ReceiveDeliveryCommand toCommand(ReceiveDeliveryRequest request);
    
    /**
     * Maps DeliveryBox to DeliveryResponse
     */
    @Mapping(target = "deliveryId", source = "id")
    @Mapping(target = "totalQuantity", source = "totalQuantity")
    @Mapping(target = "validationDate", source = "validationDate")
    @Mapping(target = "totalCost", source = "totalCost.amount")
    @Mapping(target = "unitCost", source = "unitCost.amount")
    @Mapping(target = "sellingPrice", source = "sellingPrice.amount")
    @Mapping(target = "profitMarginPercentage", source = "profitMargin")
    @Mapping(target = "availableBaskets", source = "deliveryBox", qualifiedByName = "getAvailableCount")
    @Mapping(target = "receivedAt", source = "receivedAt")
    @Mapping(target = "message", constant = "Delivery received successfully")
    DeliveryResponse toDeliveryResponse(DeliveryBox deliveryBox);
    
    @Named("getAvailableCount")
    default Long getAvailableCount(DeliveryBox deliveryBox) {
        return deliveryBox.getAvailableCount();
    }
    
    // ======== Basket Sale Mappings ========
    
    /**
     * Maps SellBasketsRequest to SellBasketsCommand
     */
    @Mapping(target = "quantity", source = "quantity")
    SellBasketsUseCase.SellBasketsCommand toCommand(SellBasketsRequest request);
    
    /**
     * Maps SellBasketsResult to SellBasketsResponse
     */
    @Mapping(target = "soldBasketIds", source = "soldBasketIds")
    @Mapping(target = "totalSold", source = "totalSold")
    @Mapping(target = "message", source = "message")
    SellBasketsResponse toResponse(SellBasketsUseCase.SellBasketsResult result);
    
    // ======== Dispose Expired Baskets Mappings ========
    
    /**
     * Maps DisposeExpiredBasketsResult to DisposeExpiredBasketsResponse
     */
    @Mapping(target = "disposedBasketIds", source = "disposedBasketIds")
    @Mapping(target = "totalDisposed", source = "totalDisposed")
    @Mapping(target = "message", source = "message")
    DisposeExpiredBasketsResponse toResponse(DisposeExpiredBasketsUseCase.DisposeExpiredBasketsResult result);
    
    // ======== Stock Info Mappings ========
    
    /**
     * Maps StockInfo to StockInfoResponse
     */
    @Mapping(target = "totalBaskets", source = "totalBaskets")
    @Mapping(target = "availableBaskets", source = "availableBaskets")
    @Mapping(target = "soldBaskets", source = "soldBaskets")
    @Mapping(target = "disposedBaskets", source = "disposedBaskets")
    @Mapping(target = "expiredBaskets", source = "expiredBaskets")
    @Mapping(target = "totalInventoryValue", source = "totalInventoryValue")
    StockInfoResponse toResponse(CheckStockUseCase.StockInfo stockInfo);
    
    // ======== Cash Register Mappings ========
    
    /**
     * Maps CashRegisterInfo to CashRegisterResponse
     */
    @Mapping(target = "totalRevenue", source = "totalRevenue")
    @Mapping(target = "totalCost", source = "totalCost")
    @Mapping(target = "grossProfit", source = "grossProfit")
    @Mapping(target = "profitMargin", source = "profitMargin")
    @Mapping(target = "totalBasketsSold", source = "totalBasketsSold")
    CashRegisterResponse toResponse(GetCashRegisterUseCase.CashRegisterInfo cashRegisterInfo);
}
