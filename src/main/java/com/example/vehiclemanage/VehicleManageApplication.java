package com.example.vehiclemanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.example.vehiclemanage.dao")
public class VehicleManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleManageApplication.class, args);
    }

}
