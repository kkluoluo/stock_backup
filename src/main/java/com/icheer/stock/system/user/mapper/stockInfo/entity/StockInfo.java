package com.icheer.stock.system.user.mapper.stockInfo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 个股信息
 *
 * @author luoxiaoying
 * @create 2022-03-18 11:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("Astock_list")
@ApiModel(value ="Astock_list",description = "个股信息表")
public class StockInfo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    /** 股票代码*/
    private String code;
    /** 股票代码*/
    private String tsCode;
    /** 股票简称*/
    private String name;
    /** 总股本*/
    private String totalStocks;
    /** 流通股*/
    private String floatStocks;
    /**所属行业 */
    private String industry;
    /** 上市日期*/
    private LocalDate listDate;
    /** 总市值*/
    private String totalCapitalization;
    /** 流通市值*/
    private String floatCapitalization;
    /** 板块*/
    private String plate;
    /** 沪深300权重股标志位*/
    private String csi300Flag;
    /** 沪深300权重股--权重*/
    private double csi300Weight;
    /** 删除标志位*/
    private String deleted;
}
