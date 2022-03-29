package com.icheer.stock.system.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icheer.stock.framework.jwt.JwtConfig;
import com.icheer.stock.system.user.entity.Code2SessionResponse;
import com.icheer.stock.system.user.entity.User;
import com.icheer.stock.system.user.entity.WxUser;
import com.icheer.stock.system.user.mapper.UserMapper;
import com.icheer.stock.system.user.service.UserService;
import com.icheer.stock.system.user.service.WxLoginService;


import com.icheer.stock.util.BeanUtils;
import com.icheer.stock.util.BusinessException;
import com.icheer.stock.util.HttpUtils;
import com.icheer.stock.util.JSONUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class WxLoginServiceImpl extends ServiceImpl<UserMapper, User> implements WxLoginService {
    private static final Logger logger = LoggerFactory.getLogger(WxLoginServiceImpl.class);

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtConfig jwtConfig;

    @Autowired
    private  UserMapper userMapper;

    @Autowired
    private UserService userService;
    /**
     * 微信小程序登录
     *
     */
    private  String wxOpenScope = "";

    @Value("${shiro.user.indexUrl}")
    private String indexUrl;

    @Value("${wx.applet.appid}")
    private String appid;

    @Value("${wx.applet.appsecret}")
    private String appSecret;
    /**
     * 微信的 code2session 接口 获取微信用户信息
     * 官方说明 : https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
     */
    private String code2Session(String jsCode) {
        String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("appid", appid);
        params.add("secret", appSecret);
        params.add("js_code", jsCode);
        params.add("grant_type", "authorization_code");
        URI code2Session = HttpUtils.getURIwithParams(code2SessionUrl, params);

        return restTemplate.exchange(code2Session, HttpMethod.GET, new HttpEntity<String>(new HttpHeaders()), String.class).getBody();
    }

    private String getAccessToken() {
        //1 从redis 查询，查不到再重新获取
        String accessToken = stringRedisTemplate.opsForValue().get("JWT-server-access-token");
        if(accessToken != null) return  accessToken;
        String code2SessionUrl = "https://api.weixin.qq.com/cgi-bin/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("appid", appid);
        params.add("secret", appSecret);
        params.add("grant_type", "client_credential");
        URI code2Session = HttpUtils.getURIwithParams(code2SessionUrl, params);
        String token = restTemplate.exchange(code2Session, HttpMethod.GET, new HttpEntity<String>(new HttpHeaders()), String.class).getBody();
        stringRedisTemplate.opsForValue().set("JWT-server-access-token", token,  3600, TimeUnit.SECONDS);
        return token;
    }

    private String msgCheck(String content) {
        //1 . code2session返回JSON数据
        String resultJson = getAccessToken();
        //2 . 解析数据
        Code2SessionResponse response = JSONUtil.toJavaObject(resultJson, Code2SessionResponse.class);

        String checkUrl = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token="+response.getAccess_token();
        HttpHeaders headers = new HttpHeaders();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("content", content);
        HttpEntity<String> request =
                new HttpEntity<String>(personJsonObject.toString(), headers);

        String personResultAsJsonStr =
                restTemplate.postForObject(checkUrl, request, String.class);
        return personResultAsJsonStr;
    }


    @Override
    public WxUser wxUserLogin(WxUser wxUserInfo){

        //1 . code2session返回JSON数据
        String resultJson = code2Session(wxUserInfo.getCode());
        //2 . 解析数据
        Code2SessionResponse response = JSONUtil.toJavaObject(resultJson, Code2SessionResponse.class);
        if (!response.getErrcode().equals("0"))
            throw new AuthenticationException("code2session失败 : " + response.getErrmsg());
        else {
            //3 . 先从本地数据库中查找用户是否存在
//            wxUser wxUser = wxUserRepository.findByWxOpenid(response.getOpenid());
            User wxUser = userService.findByWxOpenid(response.getOpenid());
            if (wxUser == null) {
                wxUser = new User();//不存在就新建用户
                wxUser.setAvatar(wxUserInfo.getAvatarUrl());
                wxUser.setWxOpenid(response.getOpenid());
                wxUser.setCreateTime(LocalDateTime.now());
            }
            //4 . 更新sessionKey和 登陆时间
//            wxUser.setAvatar(wxUserInfo.getAvatarUrl());
            wxUser.setSessionKey(response.getSession_key());
            wxUser.setUpdateTime(LocalDateTime.now());
            if(wxUser.getUserId() != null)
            {
                wxUser.setLoginName(wxUserInfo.getNickName());
                userMapper.updateById(wxUser);
            } else {
                wxUser.setLoginName(wxUserInfo.getNickName());
                wxUser.setUserName(wxUserInfo.getNickName());
                userMapper.insert(wxUser);
            }
            //wxUserRepository.save(wxUser);
            //5 . JWT 返回自定义登陆态 Token, 如果已经有token直接返回存在的token并刷新时间
            wxUser = userService.findByWxOpenid(wxUser.getWxOpenid());
            WxUser wxUserBack = new WxUser();
            BeanUtils.copyBeanProp(wxUserBack, wxUser);

            String redisToken = stringRedisTemplate.opsForValue().get("JWT-SESSION-" + wxUser.getUserId());
            if(redisToken != null) {
                //返回并更新失效时间
                wxUserBack.setToken(redisToken);
                jwtConfig.updateTokenExpireTime(String.valueOf(wxUser.getUserId()));
            } else {
                String token = jwtConfig.createTokenByWxUser(wxUser);
                wxUserBack.setToken(token);
            }

            return wxUserBack;
        }
    }

    @Override
    public String getLoginCode(){
        try{
            logger.info("开始获取微信登录二维码:");
            String oauthUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
            String redirect_uri = URLEncoder.encode(indexUrl, "utf-8");
            System.out.println("redirect_uri:"+redirect_uri);
            oauthUrl =  oauthUrl.replace("APPID",appid).replace("REDIRECT_URI",redirect_uri).replace("SCOPE",wxOpenScope);
            logger.info("oauthUrl:"+oauthUrl);
            return oauthUrl;
        }catch (Exception e)
        {
            logger.info("获取微信登录二维码失败:"+e);
            return "";
        }

    }


    /** 获取小程序 access_token
     //https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
     */

    @Override
    public Code2SessionResponse msgSecCheck(String content) {
        //1 . code2session返回JSON数据
        String resultJson = msgCheck(content);
        //2 . 解析数据
        Code2SessionResponse response = JSONUtil.toJavaObject(resultJson, Code2SessionResponse.class);
        return response;
    }



    @Override
    public WxUser loginById(Long userId){
        User wxUser = userMapper.selectById(userId);
        if(wxUser == null)
            throw new BusinessException("该用户不存在");
        //5 . JWT 返回自定义登陆态 Token, 如果已经有token直接返回存在的token并刷新时间
        WxUser wxUserBack = new WxUser();
        BeanUtils.copyBeanProp(wxUserBack, wxUser);

        String redisToken = stringRedisTemplate.opsForValue().get("JWT-SESSION-" + wxUser.getUserId());
        if(redisToken != null) {
            //返回并更新失效时间
            wxUserBack.setToken(redisToken);
            jwtConfig.updateTokenExpireTime(String.valueOf(wxUser.getUserId()));
        } else {
            String token = jwtConfig.createTokenByWxUser(wxUser);
            wxUserBack.setToken(token);
        }
        return wxUserBack;
    }
}

