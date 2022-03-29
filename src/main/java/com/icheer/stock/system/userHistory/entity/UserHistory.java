package com.icheer.stock.system.userHistory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索记录
 *
 * @author luoxiaoying
 * @create 2022-03-18 11:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("search_history")
@ApiModel(value ="search_history",description = "搜索历史记录")
public class UserHistory implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    /** 用户id*/
    private int userId;
    /** 股票代码*/
    private String tsCode;
    /** CREATE TIME*/
    private LocalDateTime createTime;
    /** 备注*/
    private String  remark;
    /** 股票名搜索*/
    private String  name;

}
