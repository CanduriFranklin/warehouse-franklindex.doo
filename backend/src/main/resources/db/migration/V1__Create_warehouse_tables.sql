-- V1__Create_warehouse_tables.sql
-- Initial database schema for Warehouse Microservice

-- Create delivery_boxes table
CREATE TABLE delivery_boxes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_quantity BIGINT NOT NULL CHECK (total_quantity > 0),
    validation_date DATE NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL CHECK (total_cost >= 0),
    unit_cost DECIMAL(10, 2) NOT NULL CHECK (unit_cost >= 0),
    selling_price DECIMAL(10, 2) NOT NULL CHECK (selling_price >= 0),
    profit_margin DOUBLE PRECISION NOT NULL CHECK (profit_margin >= 0),
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT check_selling_price_greater_than_cost CHECK (selling_price >= unit_cost)
);

-- Create basic_baskets table
CREATE TABLE basic_baskets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    validation_date DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'SOLD', 'DISPOSED', 'RESERVED')),
    delivery_box_id UUID,
    sold_at TIMESTAMP,
    disposed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_basket_delivery_box FOREIGN KEY (delivery_box_id) 
        REFERENCES delivery_boxes(id) ON DELETE SET NULL
);

-- Create indexes for performance
CREATE INDEX idx_delivery_received_at ON delivery_boxes(received_at DESC);
CREATE INDEX idx_delivery_validation_date ON delivery_boxes(validation_date);

CREATE INDEX idx_basket_validation_date ON basic_baskets(validation_date);
CREATE INDEX idx_basket_status ON basic_baskets(status);
CREATE INDEX idx_basket_created_at ON basic_baskets(created_at DESC);
CREATE INDEX idx_basket_delivery_box_id ON basic_baskets(delivery_box_id);

-- Create comments for documentation
COMMENT ON TABLE delivery_boxes IS 'Stores information about deliveries received by the warehouse';
COMMENT ON TABLE basic_baskets IS 'Stores individual basic food baskets in inventory';

COMMENT ON COLUMN delivery_boxes.total_quantity IS 'Total number of baskets in this delivery';
COMMENT ON COLUMN delivery_boxes.validation_date IS 'Expiration date for baskets in this delivery';
COMMENT ON COLUMN delivery_boxes.total_cost IS 'Total cost paid for this delivery';
COMMENT ON COLUMN delivery_boxes.unit_cost IS 'Cost per basket calculated from total cost';
COMMENT ON COLUMN delivery_boxes.selling_price IS 'Selling price per basket with profit margin applied';
COMMENT ON COLUMN delivery_boxes.profit_margin IS 'Profit margin percentage applied (e.g., 0.20 for 20%)';

COMMENT ON COLUMN basic_baskets.status IS 'Current status: AVAILABLE, SOLD, DISPOSED, or RESERVED';
COMMENT ON COLUMN basic_baskets.sold_at IS 'Timestamp when basket was sold';
COMMENT ON COLUMN basic_baskets.disposed_at IS 'Timestamp when expired basket was disposed';
