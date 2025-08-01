CREATE TABLE cards (
    id VARCHAR2(36) PRIMARY KEY,
    cardholder_name VARCHAR2(255) NOT NULL,
    balance NUMBER(19,2) DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,
    version NUMBER(19,0) DEFAULT 0 NOT NULL,
    CONSTRAINT chk_cards_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_cards_status_valid CHECK (status IN ('ACTIVE', 'BLOCKED'))
);

CREATE TABLE transactions (
    id VARCHAR2(36) PRIMARY KEY,
    card_id VARCHAR2(36) NOT NULL,
    type VARCHAR2(10) NOT NULL,
    amount NUMBER(19,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_transactions_card_id FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE,
    CONSTRAINT chk_transactions_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_transactions_type_valid CHECK (type IN ('SPEND', 'TOPUP'))
);

CREATE INDEX idx_transactions_card_id ON transactions(card_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);
CREATE INDEX idx_transactions_card_id_created_at ON transactions(card_id, created_at DESC);
CREATE INDEX idx_cards_status ON cards(status);