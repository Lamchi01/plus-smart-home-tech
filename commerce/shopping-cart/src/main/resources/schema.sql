CREATE TABLE IF NOT EXISTS shopping_carts
(
    shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username         VARCHAR NOT NULL,
    state            VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS products_in_shopping_carts
(
    shopping_cart_id UUID REFERENCES shopping_carts (shopping_cart_id),
    product_id       UUID,
    quantity         INTEGER,
    PRIMARY KEY (shopping_cart_id, product_id)
);
