package com.example.vehiclemanage.view;

import lombok.Data;

@Data
public class UserReq {
    private Integer pageNo;
    private Integer pageSize;
    private String username;
    private String nickname;
}
