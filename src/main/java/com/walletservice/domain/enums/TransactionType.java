package com.walletservice.domain.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    DEPOSIT("DEPOSIT"),
    WITHDRAW("WITHDRAW"),
    TRANSFER("TRANSFER"),;

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }

}