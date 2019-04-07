package com.example.vehiclemanage;

import com.alibaba.fastjson.JSONObject;
import com.example.vehiclemanage.dao.VehicleMapper;
import com.example.vehiclemanage.entity.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class VehicleManageApplicationTests {

    @Resource
    private VehicleMapper vehicleMapper;

    @Test
    public void contextLoads() {
        LocalDateTime ys = LocalDate.now().minusDays(1).atTime(0, 0, 0);
        LocalDateTime ye = LocalDate.now().minusDays(1).atTime(0, 0, 0);
//        log.info("---#{}",LocalDate.now().minusDays(1));
//        log.info("---#{}",LocalDate.now().minusWeeks(1));
//        log.info("---#{}",LocalDate.now().minusMonths(1));
//        log.info("---#{}", ys.toInstant(ZoneOffset.of("+8")).toEpochMilli());
//        log.info("---#{}", ye.toInstant(ZoneOffset.of("+8")).toEpochMilli());
//        Vehicle vehicle1 = vehicleMapper.selectByPrimaryKey(2);
//        log.info("vehicle#{}", JSONObject.toJSONString(vehicle1));


        try (Stream<String> lines = Files.lines(Paths.get("D:\\example.txt"), StandardCharsets.UTF_8)) {
            lines.forEach(item -> vehicleMapper.insertSelective(JSONObject.parseObject(item, Vehicle.class)));
        } catch (Exception e) {

        }


    }

}
