package com.example.vehiclemanage.service;

import com.alibaba.fastjson.JSONObject;
import com.example.vehiclemanage.dao.RecordMapper;
import com.example.vehiclemanage.dao.UserMapper;
import com.example.vehiclemanage.dao.VehicleMapper;
import com.example.vehiclemanage.entity.RecordExample;
import com.example.vehiclemanage.entity.User;
import com.example.vehiclemanage.entity.UserExample;
import com.example.vehiclemanage.entity.VehicleExample;
import com.example.vehiclemanage.util.Consts;
import com.example.vehiclemanage.util.Result;
import com.example.vehiclemanage.view.UserReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private VehicleMapper vehicleMapper;
    @Resource
    private RecordMapper recordMapper;
    @Resource
    private LogService logService;

    @Resource
    private HttpSession session;

    public Result checkAndLogin(String username, String password) throws Exception {
        UserExample example = new UserExample();
        example.createCriteria()
                .andUsernameEqualTo(username)
                .andPasswordEqualTo(Base64Utils.encodeToString(password.getBytes("utf-8")));
        List<User> users = userMapper.selectByExample(example);
        if (users.size() > 0) {
            User user = users.get(0);
            JSONObject data = new JSONObject();
            data.fluentPut("username", user.getUsername())
                    .fluentPut("nickname", user.getNickname())
                    .fluentPut("uid", user.getId())
                    .fluentPut("role", user.getRole());

            session.setAttribute(Consts.SEESION_UNAME, user.getUsername());
            session.setAttribute(Consts.SEESION_UID, user.getId());
            session.setMaxInactiveInterval(30 * 60);

            logService.add("login");

            return Result.builder().code(0).msg("success").data(data).build();
        }
        return Result.builder().code(-1).msg("用户名或者密码错误").build();
    }

    public Result add(User user) throws Exception {
        Integer id = user.getId();
        Date now = new Date();
        user.setUpdateTs(now);
        int i;
        if (id != null) {
            user.setPassword(null);
            i = userMapper.updateByPrimaryKeySelective(user);
        } else {
            user.setCreateTs(now);
            user.setPassword(Base64Utils.encodeToString(user.getPassword().getBytes("utf-8")));
            i = userMapper.insertSelective(user);
        }
        if (i > 0) {
            log.info("save or update user successfully,user#{}", user);
            return Result.builder().code(0).msg("success").build();
        }
        return Result.builder().code(-1).msg("fail").build();
    }

    @Transactional
    public Result delete(Integer id) {
        int i = userMapper.deleteByPrimaryKey(id);
        log.info("delete user successfully,userId#{}", id);

        VehicleExample vehicleExample = new VehicleExample();
        vehicleExample.createCriteria().andUidEqualTo(id);
        int j = vehicleMapper.deleteByExample(vehicleExample);
        log.info("delete vehicle successfully,j#{}", j);

        RecordExample recordExample = new RecordExample();
        recordExample.createCriteria().andUidEqualTo(id);
        int k = recordMapper.deleteByExample(recordExample);
        log.info("delete record successfully,k#{}", k);
        if (i > 0 && j >= 0 && k >= 0) {
            return Result.builder().code(0).msg("success").build();
        }
        return Result.builder().code(-1).msg("fail").build();
    }

    public Result select(UserReq req) {
        UserExample example = new UserExample();
        long total = userMapper.countByExample(example);
        if (req.getPageNo() != null && req.getPageSize() != null) {
            example.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            example.setPageSize(req.getPageSize());
        }
//        UserExample.Criteria criteria = example.createCriteria();
//        if (!StringUtils.isEmpty(req.getUsername())) {
//            criteria.andUsernameLike(req.getUsername() + "%");
//        }
        JSONObject data = new JSONObject();
        List<User> users = userMapper.selectByExample(example);
        users.forEach(u -> u.setPassword(""));
        data.fluentPut("list", users).fluentPut("total", total);
        return Result.builder().msg("success").data(data).build();
    }

    /*** 修改密码 ***/
    public Result updatePassword(String oldPassword, String newPassword) throws Exception {
        Integer id = (Integer) session.getAttribute(Consts.SEESION_UID);
        if (id != null) {
            User user = userMapper.selectByPrimaryKey(id);
            if (!user.getPassword().equals(Base64Utils.encodeToString(oldPassword.getBytes("utf-8")))) {
                return Result.builder().code(-1).msg("旧密码错误").build();
            }
            user.setPassword(Base64Utils.encodeToString(newPassword.getBytes("utf-8")));
            int i = userMapper.updateByPrimaryKeySelective(user);
            if (i > 0) {
                return Result.builder().code(0).msg("success").build();
            }
        }
        return Result.builder().code(-1).msg("fail").build();
    }

    /*** 首页统计 ***/
    public Result statistics(String role, String uid) {
        JSONObject data = new JSONObject();
        Result.ResultBuilder result = Result.builder().code(0).msg("success");
        if (role.equals("ADMIN")) {
            int adminCount = countByRole("ADMIN");
            int jgsCount = countByRole("JGS");
            int policeManCount = countByRole("POLICEMAN");
            int userCount = countByRole("USER");
            int total = countByRole(null);
            data.fluentPut("adminCount", adminCount).fluentPut("jgsCount", jgsCount).fluentPut("policeManCount", policeManCount)
                    .fluentPut("userCount", userCount).fluentPut("total", total);
            return result.data(data).build();
        } else if (role.equals("JGS")) {
            long carCount = vehicleMapper.countByExample(new VehicleExample());
            RecordExample example = new RecordExample();
            example.createCriteria().andStatusEqualTo(0);
            long unclosedCount = recordMapper.countByExample(example);
            example.clear();
            example.createCriteria().andStatusEqualTo(1);
            long closedCount = recordMapper.countByExample(example);
            data.fluentPut("carCount", carCount).fluentPut("unclosedCount", unclosedCount).fluentPut("closedCount", closedCount);
            return result.data(data).build();
        } else if (role.equals("POLICEMAN")) {
            LocalDate now = LocalDate.now();
            LocalDateTime nowDateTime = LocalDateTime.now();
            LocalDate y = now.minusDays(1);
            LocalDate w = now.minusWeeks(1);
            LocalDate m = now.minusMonths(1);

            String ts = parse(now.atTime(0, 0, 0));
            String te = parse(nowDateTime);

            String ys = parse(y.atTime(0, 0, 0));
            String ye = parse(y.atTime(23, 59, 59));

            String ws = parse(w.atTime(0, 0, 0));
            String we = parse(nowDateTime);

            String ms = parse(m.atTime(0, 0, 0));
            String me = parse(nowDateTime);

            RecordExample example = new RecordExample();

            example.createCriteria().andTimeBetween(ts, te);
            long todayCount = recordMapper.countByExample(example);

            example.clear();
            example.createCriteria().andTimeBetween(ys, ye);
            long yesterdayCount = recordMapper.countByExample(example);

            example.clear();
            example.createCriteria().andTimeBetween(ws, we);
            long weekCount = recordMapper.countByExample(example);

            example.clear();
            example.createCriteria().andTimeBetween(ms, me);
            long MonthCount = recordMapper.countByExample(example);

            data.fluentPut("todayCount", todayCount).fluentPut("yesterdayCount", yesterdayCount).fluentPut("weekCount", weekCount).fluentPut("MonthCount", MonthCount);
            return result.data(data).build();
        } else if (role.equals("USER")) {
            VehicleExample example = new VehicleExample();
            example.createCriteria().andUidEqualTo(Integer.valueOf(uid));
            long userCarCount = vehicleMapper.countByExample(example);

            RecordExample recordExample = new RecordExample();
            recordExample.createCriteria().andUidEqualTo(Integer.valueOf(uid));
            long peccancyCount = recordMapper.countByExample(recordExample);
            recordExample.clear();
            recordExample.createCriteria().andUidEqualTo(Integer.valueOf(uid)).andPaymentStatusEqualTo(0);
            long unpaidCount = recordMapper.countByExample(recordExample);

            User user = userMapper.selectByPrimaryKey(Integer.valueOf(uid));

            data.fluentPut("userCarCount", userCarCount).fluentPut("peccancyCount", peccancyCount)
                    .fluentPut("unpaidCount", unpaidCount).fluentPut("phoneNumber", user.getPhoneNumber()).fluentPut("email", user.getEmail());
            return result.data(data).build();
        }
        return Result.builder().code(-1).msg("fail").build();
    }

    private int countByRole(String role) {
        UserExample example = new UserExample();
        if (role != null) example.createCriteria().andRoleEqualTo(role);
        return (int) userMapper.countByExample(example);
    }

    private String parse(LocalDateTime localDateTime) {
        long l = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return String.valueOf(l);
    }
}
