package com.example.vehiclemanage.view;

import lombok.Data;

@Data
public class VehicleReq {
    private Integer pageNo;
    private Integer pageSize;
    private String nickname;
    private String plateNumber;
    private Integer type;
    private Integer uid;
}
