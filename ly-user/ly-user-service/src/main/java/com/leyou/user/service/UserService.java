package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CodecUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX="user:verify:phone:";

    public Boolean checkData(String data, Integer type) {
        User record = new User();
        // 判断类型
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        return userMapper.selectCount(record) == 0;
    }

    public void sendCode(String phone) {
        //生成key
        String key=KEY_PREFIX+phone;
        // 生成验证码
        String code= NumberUtils.generateCode(6);
        Map<String,String> msg=new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        // 保存验证码 由于发不了短信
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
        // 发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
//        // 保存验证码
//        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);

    }

    public Boolean register(User user, String code) {
        String key=KEY_PREFIX+user.getPhone();
        // 从redis取出验证码
        String codeCache=redisTemplate.opsForValue().get(key);
        // 检查验证码是否正确
        if(!code.equals(codeCache)){
            return false;
        }
        user.setId(null);
        user.setCreated(new Date());
        // 生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        // 对密码进行加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        // 写入数据库
        boolean boo = userMapper.insertSelective(user)==1;

        //如果注册成功，删除redis中的code
        if(boo){
            try {
                redisTemplate.delete(key);
            }catch (Exception e){
                log.error("删除验证码失败，code：{}",code,e);
            }
        }
        return boo;
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        // 校验用户名
        if(user==null){
            return null;
        }
        // 校验密码
        if(!user.getPassword().equals(CodecUtils.md5Hex(password,user.getSalt()))){
            return null;
        }
        // 用户名和密码都正确
        return user;
    }
}
