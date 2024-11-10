package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    WeChatProperties weChatProperties;
    @Autowired
    UserMapper userMapper;
    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //TODO 把这个方法抽取出来 ctrl alt+M 快捷键
        String openid = getOpenid(userLoginDTO);

        //判断当前用户是否是第一次使用外卖系统/判断openId是否为空，空则说明登陆失败，抛出登陆异常
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //先去数据库查是否有这个openid
        User user = userMapper.findOpenid(openid);

        //如果是新用户，自动完成注册；
        if(user==null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insertUser(user);
        }

        return user;
    }

    private String getOpenid(UserLoginDTO userLoginDTO) {
        Map<String,String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type","authorization_code");

        //调用微信接口服务，获得当前用户的openId，用httpClient 向指定地址发起请求；
        //返回json字符串，包含openid
        String json = HttpClientUtil.doGet(WX_LOGIN,map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
