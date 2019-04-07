package com.example.vehiclemanage.service;

import com.alibaba.fastjson.JSONObject;
import com.example.vehiclemanage.dao.RecordMapper;
import com.example.vehiclemanage.dao.UserMapper;
import com.example.vehiclemanage.dao.VehicleMapper;
import com.example.vehiclemanage.entity.Vehicle;
import com.example.vehiclemanage.entity.VehicleExample;
import com.example.vehiclemanage.util.Result;
import com.example.vehiclemanage.view.VehicleReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class VehicleService {
    @Resource
    private VehicleMapper vehicleMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RecordMapper recordMapper;

    @Resource
    private HttpServletRequest request;


    public Result add(Vehicle vehicle) {
        Integer id = vehicle.getId();
        vehicle.setUpdateTs(String.valueOf(System.currentTimeMillis()));
        vehicle.setBuyTs(
                String.valueOf(
                        LocalDate.parse(vehicle.getBuyTs())
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                )
        );
        int i;
        if (id != null) {
            i = vehicleMapper.updateByPrimaryKeySelective(vehicle);
        } else {
            vehicle.setCreateTs(String.valueOf(System.currentTimeMillis()));
            i = vehicleMapper.insertSelective(vehicle);
        }
        if (i > 0) {
            log.info("add or update vehicle successfully");
            return Result.builder().code(0).msg("success").build();
        }
        return Result.builder().code(-1).msg("fail").build();
    }


    @Transactional
    public Result delete(Integer id) {
        int i = vehicleMapper.deleteByPrimaryKey(id);
        if (i > 0) {
            log.info("delete vehicle successfully,vehicleId#{}", id);
            return Result.builder().code(0).msg("success").build();
        }
        return Result.builder().code(-1).msg("fail").build();
    }

    public Result select(VehicleReq req) {
        VehicleExample example = new VehicleExample();
        VehicleExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(req.getUid())) criteria.andUidEqualTo(req.getUid());
        if (!StringUtils.isEmpty(req.getNickname())) criteria.andUsernameLike(req.getNickname() + "%");
        if (!StringUtils.isEmpty(req.getPlateNumber())) criteria.andPlateNumberLike(req.getPlateNumber() + "%");
        if (!StringUtils.isEmpty(req.getType())) criteria.andTypeEqualTo(req.getType());

        long total = vehicleMapper.countByExample(example);
        if (req.getPageNo() != null && req.getPageSize() != null) {
            example.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            example.setPageSize(req.getPageSize());
        }
        JSONObject data = new JSONObject();
        List<Vehicle> list = vehicleMapper.selectByExample(example);

        data.fluentPut("list", list).fluentPut("total", total);
        return Result.builder().msg("success").data(data).build();
    }
}
