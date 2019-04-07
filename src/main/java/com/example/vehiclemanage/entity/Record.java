package com.example.vehiclemanage.entity;

import lombok.Data;

@Data
public class Record {
    private Integer id;

    private Integer uid;

    private String plateNumber;

    private String time;

    private String reason;

    private Integer paymentStatus;

    private Integer status;

    private String fine;

    private String paymentTs;

    private String username;
}