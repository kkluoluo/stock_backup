package com.icheer.stock.framework.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.icheer.stock.system.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 @component 配置一个bean，实例化一个类
 */
@Component
public class JwtConfig {

    /**
     * JWT 自定义密钥 我这里写死的
     */
    private static final String SECRET_KEY = "5371f568a45e5ab1f442c38e0932aef24447139b";

    /**
     * JWT 过期时间值 这里写死为和小程序时间一致 7200 秒，也就是两个小时
     */
    @Value("${shiro.session.expireTime}")
    private int expireTime;
    /**
     * 注入redisTemplate
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;



    /**
     * 根据微信用户登陆信息创建 token
     * 注 : 这里的token会被缓存到redis中,用作为二次验证
     * redis里面缓存的时间应该和jwt token的过期时间设置相同
     *
     * @param wxUser 微信用户信息
     * @return 返回 jwt token
     */
    public String createTokenByWxUser(User wxUser) {
        /** 使用用户的id 作为jwtid，这样发心跳包的时候就可以更新token*/
        String jwtId = wxUser.getUserId().toString();
        //JWT 随机ID,做为验证的key
        //1 . 加密算法进行签名得到token
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        String token = JWT.create()
                .withClaim("wxOpenid", wxUser.getWxOpenid())
                .withClaim("sessionKey", wxUser.getSessionKey())
                .withClaim("jwt-id", jwtId)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime * 60))  //JWT 配置过期时间的正确姿势
                .sign(algorithm);
        //2 . Redis缓存JWT, 注 : 请和JWT过期时间一致
        stringRedisTemplate.opsForValue().set("JWT-SESSION-" + jwtId, token, expireTime * 60, TimeUnit.SECONDS);
        return token;
    }

    /**
     * 校验token是否正确
     * 1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
     * 2 . 然后再对redisToken进行解密，解密成功则 继续流程 和 进行token续期
     *
     * @param token 密钥
     * @return 返回是否校验通过
     */
    public boolean verifyToken(String token) {
        try {
            //1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
            String redisToken = stringRedisTemplate.opsForValue().get("JWT-SESSION-" + getJwtIdByToken(token));
            if (!redisToken.equals(token)) return false;
            //2 . 得到算法相同的JWTVerifier
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("wxOpenid", getWxOpenIdByToken(redisToken))
                    .withClaim("sessionKey", getSessionKeyByToken(redisToken))
                    .withClaim("jwt-id", getJwtIdByToken(redisToken))
                    .acceptExpiresAt(System.currentTimeMillis() + expireTime * 60)  //JWT 正确的配置续期姿势
                    .build();
            //3 . 验证token
            verifier.verify(redisToken);
            //4 . Redis缓存JWT续期
            stringRedisTemplate.opsForValue().set("JWT-SESSION-" + getJwtIdByToken(token), redisToken, expireTime * 60, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) { //捕捉到任何异常都视为校验失败
            return false;
        }
    }

    /**
     * @description TODO 收到心跳包更新token 有效时间
     * @Param
     * @author sush
     * @date 2021/4/9 14:04
     * @return
     */
    public boolean  updateTokenExpireTime(String userId) {
        try {
            //1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
            String redisToken = stringRedisTemplate.opsForValue().get("JWT-SESSION-" + userId);
            if(redisToken != null) {
                //2 . 得到算法相同的JWTVerifier
                Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
                JWTVerifier verifier = JWT.require(algorithm)
                        .withClaim("wxOpenid", getWxOpenIdByToken(redisToken))
                        .withClaim("sessionKey", getSessionKeyByToken(redisToken))
                        .withClaim("jwt-id", getJwtIdByToken(redisToken))
                        .acceptExpiresAt(System.currentTimeMillis() + expireTime * 60)  //JWT 正确的配置续期姿势
                        .build();
                //3 . 验证token
                verifier.verify(redisToken);
                //4 . Redis缓存JWT续期
                stringRedisTemplate.opsForValue().set("JWT-SESSION-" + getJwtIdByToken(redisToken), redisToken, expireTime * 60, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) { //捕捉到任何异常都视为校验失败
            return false;
        }
    }

    //-------------//
    /**
     * 根据Token获取wxOpenId(注意坑点 : 就算token不正确，也有可能解密出wxOpenId,同下)
     */
    public String getWxOpenIdByToken(String token)  {
        return JWT.decode(token).getClaim("wxOpenid").asString();
    }

    public String getUserNameByToken(String token)  {
        return JWT.decode(token).getClaim("username").asString();
    }

    public String getUserPwdByToken(String token)  {
        return JWT.decode(token).getClaim("password").asString();
    }


    /**
     * 根据Token获取sessionKey
     */
    public String getSessionKeyByToken(String token)  {
        return JWT.decode(token).getClaim("sessionKey").asString();
    }

    /**
     * 根据Token 获取jwt-id
     */
    private String getJwtIdByToken(String token)  {
        return JWT.decode(token).getClaim("jwt-id").asString();
    }
}
