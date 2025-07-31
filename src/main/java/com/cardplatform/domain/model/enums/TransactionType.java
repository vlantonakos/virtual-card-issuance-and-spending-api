package com.cardplatform.domain.model.enums;

public enum TransactionType {
    /**
     * Transaction that reduces the card balance.
     */
    SPEND,

    /**
     * Transaction that increases the card balance.
     */
    TOPUP
}
