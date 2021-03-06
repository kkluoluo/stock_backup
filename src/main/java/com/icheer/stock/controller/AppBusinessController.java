package com.icheer.stock.controller;

import com.icheer.stock.system.user.mapper.stockInfo.entity.StockInfo;
import com.icheer.stock.system.user.mapper.stockInfo.service.StockInfoService;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.system.user.entity.WxUser;
import com.icheer.stock.system.user.service.UserService;
import com.icheer.stock.system.user.service.WxLoginService;


import com.icheer.stock.system.userHistory.service.UserHistoryService;
import com.icheer.stock.util.*;
import lombok.extern.java.Log;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.DoubleToIntFunction;
import java.util.function.ToIntFunction;

@Controller
@RequestMapping("/api")
public class AppBusinessController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(AppBusinessController.class);



    @Resource
    private UserService userService;

    @Resource
    private WxLoginService wxLoginService;

    @Resource
    private ServerVersion serverVersion;

    @Resource
    private StockInfoService stockInfoService;

    @Resource
    private TradeDataService tradeDataService;

    @Resource
    private UserHistoryService userHistoryService;




    /**
     * version
     */
    @RequestMapping("/version")
    @ResponseBody
    public Result getVersion(){
        Map<String ,String> result = new HashMap<>();
        result.put("stock-backend",serverVersion.getVersion());
        result.put("Time",LocalDateTime.now().toString());
        return new Result(200,"SUCCESS",result);
    }

    /**
     * ??????????????? loginName?????? userName??????????????????
     * @param wxLoginInfo
     * @return
     */
    @RequestMapping("/loginByMini")
    @ResponseBody
    public Result loginByMini(@RequestBody WxUser wxLoginInfo) {
        boolean result = false;
        WxUser wxLoginUserToken = new WxUser();
        try{
            wxLoginUserToken =  wxLoginService.wxUserLogin(wxLoginInfo);
            result = true;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(result == true) {
//                Map<String,String> map = new HashMap<>();
//                roles = roleService.selectRoleKeys(wxLoginUserToken.getUserId());
//                wxLoginUserToken.setRoles(roles);
                wxLoginUserToken.setAvatarUrl(wxLoginUserToken.getAvatar());
                wxLoginUserToken.setNickName(wxLoginUserToken.getLoginName());
                return new Result(200,"????????????",wxLoginUserToken);


            } else {
                return  new Result(200,"????????????","");
            }
        }
    }

    @RequestMapping("/getLoginCode")
    @ResponseBody
    public Result getLoginCode() {
        try {
            return new Result(200,"?????????????????????????????????",wxLoginService.getLoginCode());

        } catch (Exception e) {
            throw new BaseException(e.toString());
        }
    }



    /**
     * ??????????????????
     * @param stockMap(code) ????????????????????????
     * @param stockMap(name) ????????????????????????
     *
     */
    @RequestMapping("/search_stock")
    @ResponseBody
    public Result search_stockByCode(@RequestBody StockMap stockMap ){

        Long userId = (Long) SecurityUtils.getSubject().getSession().getAttribute("userId");

        List<StockTradeResult> stockTradeResults = new ArrayList<>();
        if (stockMap.getCode()!=null & stockMap.getCode()!="")
        {
            StockInfo stock= stockInfoService.getOneByCode(stockMap.getCode());
            /**??????100?????????**/
            if(stock != null)
            {
                List<TradeData> list    = tradeDataService.listDescByTradeDate("000001",100);
                StockTradeResult result = new StockTradeResult();
                result.setStockInfo(stock);
                result.setTradeDataList(list);
                stockTradeResults.add(result);
                userHistoryService.setSearchHistory(Integer.valueOf(userId.toString()),stock.getCode());
            }
        }else
        {
            ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
            stockQuery.eq("deleted",0);
            stockQuery.like("name",stockMap.getName());
            List<StockInfo> stocks= stockInfoService.list(stockQuery);
            userHistoryService.setSearchNameHistory(Integer.valueOf(userId.toString()),stockMap.getName());
            if (stocks != null)
            {
                for(StockInfo one:stocks)
                {
                    List<TradeData> list    = tradeDataService.listDescByTradeDate("000001",100);
                    StockTradeResult result = new StockTradeResult();
                    result.setStockInfo(one);
                    result.setTradeDataList(list);
                    stockTradeResults.add(result);
                }
            }
        }
        return new Result(200,"success",getDataTable(stockTradeResults));
    }

    /**
     * ??????????????????
     * @param stockMap
     * By   ????????????
     *
     */
    @RequestMapping("/search_stockByName")
    @ResponseBody
    public Result search_stockByName(@RequestBody StockMap stockMap){
        ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
        stockQuery.eq("deleted",0);
        stockQuery.like("name",stockMap.getName());
        List<StockInfo> stocks= stockInfoService.list(stockQuery);
        TableDataInfo tableDataInfo = new TableDataInfo(stocks,stocks.size());
        return  new Result(200,"success",tableDataInfo);

    }

    /**
     * ????????????????????????
     * @param  stockMap(code)  ??????????????????
     * @param  stockMap(range) ??????????????????
     * @return
     */
    @RequestMapping("/stock_analysis")
    @ResponseBody
    public Result stock_analysis(@RequestBody StockMap stockMap) throws IOException
    {
        Long userId = (Long) SecurityUtils.getSubject().getSession().getAttribute("userId");
        /** ???????????????30????????????*/
//        List<Double>  cp_open_ls = tradeDataService.getKeyList(stockMap.getCode(),"open",30);
//        List<Double>  cp_close_ls= tradeDataService.getKeyList(stockMap.getCode(),"close",30);
        String key = "ma5";
        List<Double>  cp_ls= tradeDataService.getKeyList(stockMap.getCode(),key,stockMap.getRange());
        List <StockInfo> CSI300list=stockInfoService.getCSI300List();
        Integer total_ranges = 600;
        Integer window_len   = 30;
        List<Double> k_list = new ArrayList<>();
        Map<Double,String>  k_code  =new HashMap<>();
        Map<String,Integer> code_id =new HashMap<>();
        for(StockInfo each : CSI300list)
        {
            Double each_k  = 0.00;
            int   trade_id = 0;
            List<Double> each_trades = tradeDataService.getKeyList(each.getCode(),key,total_ranges);
            List<String>  trades_ids = tradeDataService.listStringByKey(each.getCode(),"id",total_ranges);
            if (each_trades.size()<total_ranges) total_ranges = each_trades.size();

            for(Integer i =0;i<total_ranges;i=i+window_len)
            {
                if(i+stockMap.getRange()>=total_ranges)
                {break;}
                List<Double> each_ls = each_trades.subList(i,stockMap.getRange()+i);
                Double K_like =getPearsonBydim(cp_ls,each_ls);
                if(K_like>each_k){
                    each_k   = K_like;
                    trade_id = Integer.valueOf(trades_ids.get(i));
                }
            }
            k_list.add(each_k);
            k_code.put(each_k,each.getCode());
            code_id.put(each.getCode(),trade_id);
        }
        Collections.sort(k_list,Collections.reverseOrder());
        List<StockSimilar> similarList = new ArrayList<>();
        for( double similar:k_list.subList(0,10))
        {
            StockSimilar stockSimilar = new StockSimilar();
            stockSimilar.setSimilar(similar);
            String code = k_code.get(similar);
            stockSimilar.setCode(code);
            stockSimilar.setName(stockInfoService.getOneByCode(code).getName());
            stockSimilar.setTradeData(tradeDataService.getTradeSinceId(code,code_id.get(code),stockMap.getRange()+20));
            similarList.add(stockSimilar);
        }
        System.out.println(k_list);
        userHistoryService.setSearchHistory(Integer.valueOf(userId.toString()),stockMap.getCode());
        return new Result(200,"",similarList);
    }

    /**
     * ??????????????????????????????
     * @param ratingOne
     * @param ratingTwo
     * @return
     */
    public Double getPearsonBydim(List<Double> ratingOne, List<Double> ratingTwo) {
        try {
            if(ratingOne.size() != ratingTwo.size()) {//???????????????????????????????????????????????????????????????????????????
                if(ratingOne.size() > ratingTwo.size()) {//?????????????????????
                    List<Double> temp = ratingOne;
                    ratingOne = new ArrayList<>();
                    for(int i=0;i<ratingTwo.size();i++) {
                        ratingOne.add(temp.get(i));
                    }
                }else {
                    List<Double> temp = ratingTwo;
                    ratingTwo = new ArrayList<>();
                    for(int i=0;i<ratingOne.size();i++) {
                        ratingTwo.add(temp.get(i));
                    }
                }
            }
            double sim = 0D;//?????????????????????????????????
            double commonItemsLen = ratingOne.size();//??????????????????
            double oneSum = 0D;//????????????????????????
            double twoSum = 0D;//????????????????????????
            double oneSqSum = 0D;//??????????????????????????????
            double twoSqSum = 0D;//??????????????????????????????
            double oneTwoSum = 0D;//???????????????????????????
            for(int i=0;i<ratingOne.size();i++) {//??????
                double oneTemp = ratingOne.get(i);
                double twoTemp = ratingTwo.get(i);
                //??????
                oneSum += oneTemp;
                twoSum += twoTemp;
                oneSqSum += Math.pow(oneTemp, 2);
                twoSqSum += Math.pow(twoTemp, 2);
                oneTwoSum += oneTemp*twoTemp;
            }
            double num = (commonItemsLen*oneTwoSum) - (oneSum*twoSum);
            double den = Math.sqrt((commonItemsLen * oneSqSum - Math.pow(oneSum, 2)) * (commonItemsLen * twoSqSum - Math.pow(twoSum, 2)));
            sim = (den == 0) ? 1 : num / den;
            return sim;
        } catch (Exception e) {
            return null;
        }
    }
    /** ???????????????????????? int???????????????????????????????????????and????????????0.0*/
    public static Double getPearsonBydim2(List<Double> ratingOne, List<Double> ratingTwo) {
        if(ratingOne.size() != ratingTwo.size()) {//???????????????????????????????????????????????????????????????????????????
            return null;
        }
        double sim = 0D;//?????????????????????????????????
        int commonItemsLen = ratingOne.size();//??????????????????
        int oneSum = 0;//????????????????????????
        int twoSum = 0;//????????????????????????
        for(int i=0; i<commonItemsLen; i++) {
            oneSum += ratingOne.get(i)*100;
            twoSum += ratingTwo.get(i)*100;
        }
        int oneAvg = oneSum/commonItemsLen;//??????????????????????????????
        int twoAvg = twoSum/commonItemsLen;//??????????????????????????????
        int sonSum = 0;
        int tempOne = 0;
        int tempTwo = 0;
        for(int i=0; i<commonItemsLen; i++) {
            sonSum += (ratingOne.get(i)*100-oneAvg)*(ratingTwo.get(i)-twoAvg);
            tempOne += Math.pow((ratingOne.get(i)*100-oneAvg), 2);
            tempTwo += Math.pow((ratingTwo.get(i)*100-twoAvg), 2);
        }
        double fatherSum = Math.sqrt(tempOne * tempTwo);
        sim = (fatherSum == 0) ? 1 : sonSum / fatherSum;
        return sim;
    }







}
