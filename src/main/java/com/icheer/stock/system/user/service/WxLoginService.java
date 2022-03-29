package com.icheer.stock.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.user.entity.Code2SessionResponse;
import com.icheer.stock.system.user.entity.User;
import com.icheer.stock.system.user.entity.WxUser;

public interface WxLoginService extends IService<User> {
    public WxUser wxUserLogin(WxUser wxUserInfo);
    public User loginById(Long userId);
    public String getLoginCode();
    public Code2SessionResponse msgSecCheck(String content);

}
