package com.icheer.stock.system.tradeData.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.icheer.stock.system.user.mapper.stockInfo.entity.StockInfo;
import com.icheer.stock.system.user.mapper.stockInfo.mapper.StockInfoMapper;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.mapper.TradeDataMapper;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.util.ExcludeEmptyQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeDataServiceImpl extends ServiceImpl<TradeDataMapper, TradeData> implements TradeDataService {
    private static final Logger log = LoggerFactory.getLogger(TradeDataServiceImpl.class);

    @Autowired
    private  TradeDataMapper tradeDataMapper;
    @Autowired
    private StockInfoMapper stockInfoMapper;

    /**get list By table_name */
    public List<TradeData> list(String code){
        ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
        stockQuery.eq("deleted",0);
        stockQuery.eq("code",code);
        StockInfo stockInfo = stockInfoMapper.selectOne(stockQuery);
        String  table_name = stockInfo.getTsCode().replace(".","_");
        return tradeDataMapper.list(table_name);
    }

    /**倒序 最新 100 条记录 */
    public List<TradeData> listDescByTradeDate(String code, Integer range){

        return tradeDataMapper.listDescByTradeDate(tableName_code(code),range);
    }

    /**倒序 获取字段key 列表 */
    public List<Double> getKeyList(String code, String key ,Integer range){

        String  table_name = tableName_code(code);
        return tradeDataMapper.getKeyList(table_name,key,range);
    }

    /**倒序 获取字段key 列表 */
    public List<String> listStringByKey(String code, String key ,Integer range){

        String  table_name = tableName_code(code);
        return tradeDataMapper.getStringKeyList(table_name,key,range);
    }

    /**获取getTradeSinceId列表 */
    public List<TradeData> getTradeSinceId(String code,  Integer id, Integer range){
        String  table_name = tableName_code(code);
        return tradeDataMapper.getTradeSinceId(table_name,id,range);

    }

    String tableName_code(String code)
    {
        ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
        stockQuery.eq("deleted",0);
        stockQuery.eq("code",code);
        StockInfo stockInfo = stockInfoMapper.selectOne(stockQuery);
        String  table_name = stockInfo.getTsCode().replace(".","_");
        return table_name;
    }
}



