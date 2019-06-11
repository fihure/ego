package com.ego.user.service;

import com.ego.common.utils.CodecUtils;
import com.ego.common.utils.NumberUtils;
import com.ego.user.mapper.UserMapper;
import com.ego.user.pojo.User;
import javafx.beans.binding.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "user:code:phone:";
    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(user) == 0;
    }

    public Boolean sendVerifyCode(String phone) {
        // 生成验证码
        String code = NumberUtils.generateCode(6);
        try {
            // 发送短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            this.amqpTemplate.convertAndSend("ego.sms.exchange", "sms.verify.code", msg);
            // 将code存入redis
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 60, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            logger.error("发送短信失败。phone：{}， code：{}", phone, code);
            return false;
        }
    }

    public Boolean register(User user, String code) {
        String key=KEY_PREFIX+user.getPhone();
        //从redis中取出验证码
        String cacheCode = this.redisTemplate.opsForValue().get(key);
        //检验验证码是否正确
        if (code.equals(cacheCode)){
            user.setCreated(new Date());
            String passwordBcryptEncode = CodecUtils.passwordBcryptEncode(user.getUsername(), user.getPassword());
            user.setPassword(passwordBcryptEncode);
            //写入数据库
            int i = this.userMapper.insertSelective(user);
            //如果注册成功，删除redis中的验证码
            if(i>0){
                this.redisTemplate.delete(key);
                return true;
            }
        }
        return false;
    }
}
