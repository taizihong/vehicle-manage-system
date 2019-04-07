package com.example.vehiclemanage.service;

import com.alibaba.fastjson.JSONObject;
import com.example.vehiclemanage.dao.LogMapper;
import com.example.vehiclemanage.entity.Log;
import com.example.vehiclemanage.entity.LogExample;
import com.example.vehiclemanage.util.Consts;
import com.example.vehiclemanage.util.Result;
import com.example.vehiclemanage.view.LogReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Service
public class LogService {
    @Resource
    private LogMapper logMapper;

    @Resource
    private HttpSession session;

    public void add(String content) {
        Log log = new Log();
        log.setContent(content);
        log.setUid((Integer) session.getAttribute(Consts.SEESION_UID));
        log.setUsername((String) session.getAttribute(Consts.SEESION_UNAME));
        logMapper.insertSelective(log);
    }

    public Result select(LogReq req) {
        LogExample example = new LogExample();
        long total = logMapper.countByExample(new LogExample());
        if (req.getPageNo() != null && req.getPageSize() != null) {
            example.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            example.setPageSize(req.getPageSize());
        }
        List<Log> list = logMapper.selectByExample(example);
        JSONObject data = new JSONObject();
        data.fluentPut("list", list).fluentPut("total", total);
        return Result.builder().code(0).msg("success").data(data).build();
    }
}
