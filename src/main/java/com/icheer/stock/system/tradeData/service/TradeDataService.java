package com.icheer.stock.system.tradeData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.tradeData.entity.TradeData;

import java.util.List;

public interface TradeDataService  extends IService<TradeData> {

    /**get list By table_name */
    public List<TradeData> list(String code);

    /**倒序 最新 100 条记录 */
    public List<TradeData> listDescByTradeDate(String code, Integer range);

    /**倒序 获取字段key 列表 */
    public List<Double> getKeyList(String code, String key ,Integer range);

    /**倒序 获取字段key 列表 */
    public List<String> listStringByKey(String code, String key ,Integer range);

    /**获取getTradeSinceId列表 */
    public List<TradeData> getTradeSinceId(String code,  Integer id, Integer range);


}
