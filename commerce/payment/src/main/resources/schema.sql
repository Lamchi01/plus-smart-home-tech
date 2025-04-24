CREATE TABLE IF NOT EXISTS PAYMENTS
(
    payment_id     UUID PRIMARY KEY,
    order_id       UUID             NOT NULL,
    total_payment  DOUBLE PRECISION NOT NULL,
    delivery_total DOUBLE PRECISION NOT NULL,
    fee_total      DOUBLE PRECISION NOT NULL,
    payment_state  VARCHAR(50)      NOT NULL
);