package com.icheer.stock.system.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *用户表
 * </p>
 *
 * @author luoxiao'ying
 * @since July 09 2021
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
@ApiModel(value ="User用户",description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty(value = "登陆名")
    private String loginName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "wxopenid")
    private String wxOpenid;

    @ApiModelProperty(value = "wx sessionkey")
    private String sessionKey;


    @ApiModelProperty(value = "电话")
    private String phoneNumber;


    @ApiModelProperty(value = "头像路径")
    private String avatar;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "状态")
    private String token;

    @ApiModelProperty(value = "删除标志位")
    private String deleted;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


    @ApiModelProperty(value = "是否订阅")
    private String subscribe;






}
