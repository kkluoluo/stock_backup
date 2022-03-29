package com.icheer.stock.system.user.mapper.stockInfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.user.mapper.stockInfo.entity.StockInfo;

import java.util.List;

public interface StockInfoService extends IService<StockInfo> {


    /**获取表名 */
//    public  String  getTableNameByCode(String code);

    /**getOneByCode */
    public  StockInfo  getOneByCode(String code);

    /**获取沪深300的股票list */
    public List<StockInfo> getCSI300List();
}
