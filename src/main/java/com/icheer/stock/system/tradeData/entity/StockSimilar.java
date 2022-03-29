package com.icheer.stock.system.tradeData.entity;

import lombok.Data;

import java.util.List;

@Data
public class StockSimilar{

    private  String code;
    private  String name;
    private  double similar;
    private List<TradeData> tradeData;
}
