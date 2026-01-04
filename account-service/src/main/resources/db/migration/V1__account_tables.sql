CREATE TABLE IF NOT EXISTS customers (
  customer_id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  full_name VARCHAR(200) NOT NULL,
  monthly_limit_yen INT NOT NULL DEFAULT 200000
);

CREATE TABLE IF NOT EXISTS cards (
  card_id BIGSERIAL PRIMARY KEY,
  customer_id BIGINT NOT NULL REFERENCES customers(customer_id),
  last4 VARCHAR(4) NOT NULL,
  brand VARCHAR(30) NOT NULL,
  status VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS limit_reservations (
  id BIGSERIAL PRIMARY KEY,
  saga_id VARCHAR(80) NOT NULL UNIQUE,
  customer_id BIGINT NOT NULL,
  amount_yen INT NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cards_customer_id ON cards(customer_id);
