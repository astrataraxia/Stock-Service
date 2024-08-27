package com.sydragon.customerportfolio.entity;

import org.springframework.data.annotation.Id;

public class Customer {

    @Id
    private Integer id;
    private String name;
    private Integer balance;

    public Integer id() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer balance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
