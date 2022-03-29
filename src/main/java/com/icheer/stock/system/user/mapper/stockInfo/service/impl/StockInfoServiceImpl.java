package com.icheer.stock.system.user.mapper.stockInfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.icheer.stock.system.user.mapper.stockInfo.entity.StockInfo;
import com.icheer.stock.system.user.mapper.stockInfo.mapper.StockInfoMapper;
import com.icheer.stock.system.user.mapper.stockInfo.service.StockInfoService;
import com.icheer.stock.util.ExcludeEmptyQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class StockInfoServiceImpl extends ServiceImpl<StockInfoMapper, StockInfo> implements StockInfoService {
    private static final Logger log = LoggerFactory.getLogger(StockInfoServiceImpl.class);
    /**获取表名 */
    @Resource
    private  StockInfoMapper stockInfoMapper;

//    public  String  getTableNameByCode(String code){
//        ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
//        stockQuery.eq("deleted",0);
//        stockQuery.eq("code",code);
//        StockInfo stockInfo = stockInfoMapper.selectOne(stockQuery);
//        String  table_name = stockInfo.getTsCode().replace(".","_");
//        return table_name;
//    }

    /**getOneByCode */
    public  StockInfo  getOneByCode(String code){
        ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
        stockQuery.eq("deleted",0);
        stockQuery.eq("code",code);
        return  stockInfoMapper.selectOne(stockQuery);

    }

    /**获取沪深300的股票list */
    public List<StockInfo> getCSI300List(){
        ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
        stockQuery.eq("deleted",0);
        stockQuery.eq("csi300_flag","1");
        return  stockInfoMapper.selectList(stockQuery);
    }
}

