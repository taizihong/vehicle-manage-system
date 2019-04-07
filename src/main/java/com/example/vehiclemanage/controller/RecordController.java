package com.example.vehiclemanage.controller;

import com.example.vehiclemanage.entity.Record;
import com.example.vehiclemanage.service.RecordService;
import com.example.vehiclemanage.util.Result;
import com.example.vehiclemanage.view.RecordReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
public class RecordController {

    @Resource
    private RecordService recordService;

    @PostMapping("addRecord")
    public Result select(@RequestBody Record record) {
        try {
            log.info("add record,req#{}", record);
            return recordService.add(record);
        } catch (Exception e) {
            log.error("add record error#{}", e);
            return Result.builder().code(-1).msg("添加车辆违章信息失败").build();
        }
    }

    @PostMapping("getRecord")
    public Result select(@RequestBody RecordReq req) {
        try {
            log.info("get record,req#{}", req);
            return recordService.select(req);
        } catch (Exception e) {
            log.error("get record error#{}", e);
            return Result.builder().code(-1).msg("查询车辆违章信息失败").build();
        }
    }

    @GetMapping("closeRecord")
    public Result closeRecord(Integer id) {
        try {
            log.info("close record,id#{}", id);
            Record record = new Record();
            record.setId(id);
            record.setStatus(1);
            return recordService.update(record);
        } catch (Exception e) {
            log.error("close record error#{}", e);
            return Result.builder().code(-1).msg("关闭违章记录失败").build();
        }
    }

    @GetMapping("pay")
    public Result pay(Integer id) {
        try {
            log.info("pay record,id#{}", id);
            Record record = new Record();
            record.setId(id);
            record.setPaymentTs(String.valueOf(System.currentTimeMillis()));
            record.setPaymentStatus(1);
            return recordService.update(record);
        } catch (Exception e) {
            log.error("pay record error#{}", e);
            return Result.builder().code(-1).msg("缴纳罚款失败").build();
        }
    }

    @GetMapping("deleteRecord")
    public Result deleteRecord(Integer id) {
        try {
            log.info("delete record,id#{}", id);
            return recordService.delete(id);
        } catch (Exception e) {
            log.error("delete record error#{}", e);
            return Result.builder().code(-1).msg("删除违章记录失败").build();
        }
    }


}
