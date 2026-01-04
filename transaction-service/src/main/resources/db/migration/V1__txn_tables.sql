CREATE TABLE IF NOT EXISTS transactions (
  txn_id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  card_id BIGINT NOT NULL,
  amount_yen INT NOT NULL,
  status VARCHAR(30) NOT NULL,
  txn_date TIMESTAMPTZ NOT NULL DEFAULT now(),
  merchant_name VARCHAR(200) NOT NULL,
  category VARCHAR(50) NOT NULL,
  saga_id VARCHAR(80) UNIQUE
);
CREATE INDEX IF NOT EXISTS idx_txn_user_date ON transactions(user_id, txn_date);
CREATE INDEX IF NOT EXISTS idx_txn_card_date ON transactions(card_id, txn_date);

CREATE TABLE IF NOT EXISTS statement_objects (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  s3_key VARCHAR(300) NOT NULL UNIQUE,
  original_filename VARCHAR(200) NOT NULL,
  uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_stmt_user ON statement_objects(user_id);
