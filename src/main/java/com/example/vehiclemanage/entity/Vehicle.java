package com.example.vehiclemanage.entity;

import lombok.Data;

@Data
public class Vehicle {
    private Integer id;

    private String name;

    private String plateNumber;

    private Integer uid;

    private String username;

    private String createTs;

    private String updateTs;

    private Integer type;

    private String buyTs;

    private String description;
}