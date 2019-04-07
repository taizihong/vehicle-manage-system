package com.example.vehiclemanage.view;

import lombok.Data;

@Data
public class RecordReq {
    private Integer pageNo;
    private Integer pageSize;
    private Integer uid;
}
