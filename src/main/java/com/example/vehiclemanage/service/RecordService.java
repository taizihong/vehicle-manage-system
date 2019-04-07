package com.example.vehiclemanage.service;

import com.alibaba.fastjson.JSONObject;
import com.example.vehiclemanage.dao.RecordMapper;
import com.example.vehiclemanage.dao.UserMapper;
import com.example.vehiclemanage.entity.Record;
import com.example.vehiclemanage.entity.RecordExample;
import com.example.vehiclemanage.entity.User;
import com.example.vehiclemanage.util.Result;
import com.example.vehiclemanage.view.RecordReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Service
public class RecordService {
    @Resource
    private RecordMapper recordMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private HttpServletRequest request;

    public Result add(Record record) {
        record.setTime(String.valueOf(System.currentTimeMillis()));
        int i = recordMapper.insertSelective(record);
        if (i > 0) {
            log.info("add record successfully");
            return Result.builder().code(0).msg("success").build();
        }
        return Result.builder().code(-1).msg("fail").build();
    }

    public Result select(RecordReq req) {
        RecordExample example = new RecordExample();
        if (req.getUid() != null) {
            example.createCriteria().andUidEqualTo(req.getUid());
        }
        long total = recordMapper.countByExample(example);
        example.setPageNo((req.getPageNo() - 1) * req.getPageSize());
        example.setPageSize(req.getPageSize());
        List<Record> list = recordMapper.selectByExample(example);
        list.forEach(item -> {
            if (item.getUid() != null) {
                User user = userMapper.selectByPrimaryKey(item.getUid());
                item.setUsername(user.getNickname());
            }
        });
        JSONObject data = new JSONObject();
        data.fluentPut("list", list).fluentPut("total", total);
        return Result.builder().code(0).msg("success").data(data).build();
    }

    public Result update(Record record) {
        int i = recordMapper.updateByPrimaryKeySelective(record);
        if (i > 0) {
            log.info("update record successfully");
            return Result.builder().code(0).msg("success").build();
        }
        return Result.builder().code(-1).msg("fail").build();
    }

    public Result delete(Integer id) {
        int i = recordMapper.deleteByPrimaryKey(id);
        if (i > 0) {
            log.info("delete record successfully,recordId#{}", id);
            return Result.builder().code(0).msg("success").build();
        }
        return Result.builder().code(-1).msg("fail").build();
    }
}
