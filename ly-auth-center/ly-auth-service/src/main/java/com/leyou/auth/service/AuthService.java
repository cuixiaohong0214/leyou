package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private JwtProperties jwtProp;
    @Autowired
    private UserClient userClient;
    public String authentication(String username, String password) {
        try {
            // 查询用户
            User user = userClient.queryUser(username, password);
            if(null==user){
                log.info("用户信息不存在，{}", username);
                return null;
            }
            // 生成token
            String token=JwtUtils.generateToken(new UserInfo(user.getId(),user.getUsername()),jwtProp.getPrivateKey(),jwtProp.getExpire());
            return token;
        }catch (Exception e){
            return null;
        }
    }
}
