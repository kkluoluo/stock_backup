package com.icheer.stock.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.user.entity.User;

public interface UserService extends IService<User> {


    public User findByWxOpenid(String wxOpenid);
}
