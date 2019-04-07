package com.example.vehiclemanage.controller;

import com.example.vehiclemanage.service.LogService;
import com.example.vehiclemanage.util.Result;
import com.example.vehiclemanage.view.LogReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class LogController {

    @Resource
    private LogService logService;

    @PostMapping("getLog")
    public Result getLog(@RequestBody LogReq req) {
        try {
            log.info("get log,req#{}", req);
            return logService.select(req);
        } catch (Exception e) {
            log.error("get log error#{}", e);
            return Result.builder().code(-1).msg("查询系统日志失败").build();
        }
    }

}
