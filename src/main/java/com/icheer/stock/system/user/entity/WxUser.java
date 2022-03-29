package com.icheer.stock.system.user.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * com.icheer.stock.system.user.entity
 *
 * @author eiker
 * @create 2021-01-18 11:37
 */
@Data
public class WxUser extends User{

    /**前端返回登录使用的 2*/
    @ApiModelProperty(value = "code")
    private String code;

    /**前端返回登录使用的 2*/
    @ApiModelProperty(value = "avatarUrl")
    private String avatarUrl;

    /**前端返回登录使用的 2*/
    @ApiModelProperty(value = "nickName")
    private String nickName;




}
