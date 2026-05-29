CREATE TABLE orders.orders (
    id VARCHAR(36) PRIMARY KEY,
    id_account VARCHAR(36) NOT NULL,
    date TIMESTAMP NOT NULL,
    total REAL NOT NULL
);

CREATE TABLE orders.order_items (
    id VARCHAR(36) PRIMARY KEY,
    id_product VARCHAR(36) NOT NULL,
    id_order VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL,
    total REAL NOT NULL,
    CONSTRAINT fk_order_items_orders FOREIGN KEY (id_order) REFERENCES orders.orders(id) ON DELETE CASCADE
);