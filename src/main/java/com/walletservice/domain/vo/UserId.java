package com.walletservice.domain.vo;

import lombok.Data;

@Data
public class UserId {
    private String id;

    public UserId(String id) {
        this.id = id;
    }
}