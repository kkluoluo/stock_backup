package com.icheer.stock.system.tradeData.mapper;

import com.icheer.stock.system.tradeData.entity.TradeData;
import org.apache.ibatis.javassist.runtime.Desc;
import org.apache.ibatis.jdbc.SQL;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;



public class TradeDataProvider {

    public String list(String table_name){

        return new SQL(){{
            SELECT("*");
            FROM(table_name);


        }}.toString();
    }

    public String listDescByTradeDate(String table_name, int range){
        return "select * from "+ table_name + " order by trade_date desc limit "+ String.valueOf(range);
    }

    public String getKeyList(String table_name, String key ,Integer range)
    {
        return "select "+ key + " from "+ table_name + " order by trade_date desc limit "+ String.valueOf(range);
    }

    public  String StringKeyList(String table_name, String key ,Integer range)
    {
        return "select "+ key + " from "+ table_name + " order by trade_date desc limit "+ String.valueOf(range);
    }

    public String getTradeSinceId(String table_name,  Integer id, Integer range){
        return "select * from "+ table_name +" where id between " + id.toString() +" and " +String.valueOf(id+range);
    }
}
