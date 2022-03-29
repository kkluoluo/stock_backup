package com.icheer.stock.system.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icheer.stock.system.user.entity.User;
import com.icheer.stock.system.user.mapper.UserMapper;
import com.icheer.stock.system.user.service.UserService;


import com.icheer.stock.util.ExcludeEmptyQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;

    public User findByWxOpenid(String wxOpenid)
    {
        ExcludeEmptyQueryWrapper<User> queryWrapper = new ExcludeEmptyQueryWrapper<>();
        queryWrapper.eq("wx_openid",wxOpenid);
        return userMapper.selectOne(queryWrapper);
    }



}
