package com.icheer.stock.system.tradeData.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class TradeData implements Serializable {

    @ApiModelProperty(value = "indexID")
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String      tsCode;
    private LocalDate   tradeDate;
    private  double open;
    private  double high;
    private  double low;
    private  double close;
    private  double preClose;
    private  double priceChange;
    private  double pctChg;
    private  double vol;
    private  double amount;
    private  double ma5;
    private  double ma10;
    private  double ma20;

}
