package com.example.vehiclemanage.controller;

import com.example.vehiclemanage.entity.Vehicle;
import com.example.vehiclemanage.service.VehicleService;
import com.example.vehiclemanage.util.Result;
import com.example.vehiclemanage.view.VehicleReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class VehicleController {

    @Resource
    private VehicleService vehicleService;

    @PostMapping("addVehicle")
    public Result addVehicle(@RequestBody Vehicle vehicle) {
        try {
            log.info("add or update vehicle,params#{}", vehicle);
            return vehicleService.add(vehicle);
        } catch (Exception e) {
            log.error("add or update vehicle error#{}", e);
            return Result.builder().code(-1).msg("添加或修改车辆失败").build();
        }
    }

    @RequestMapping("deleteVehicle")
    public Result deleteVehicle(Integer id) {
        try {
            log.info("delete vehicle,id#{}", id);
            return vehicleService.delete(id);
        } catch (Exception e) {
            log.error("delete vehicle error#{}", e);
            return Result.builder().code(-1).msg("删除车辆失败").build();
        }
    }

    @PostMapping("getVehicle")
    public Result getVehicle(@RequestBody VehicleReq req) {
        try {
            log.info("get vehicle,req#{}", req);
            return vehicleService.select(req);
        } catch (Exception e) {
            log.error("get vehicle error#{}", e);
            return Result.builder().code(-1).msg("查询车辆失败").build();
        }
    }

}
