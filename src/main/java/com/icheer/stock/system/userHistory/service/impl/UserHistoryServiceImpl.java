package com.icheer.stock.system.userHistory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.icheer.stock.system.user.mapper.stockInfo.service.StockInfoService;
import com.icheer.stock.system.user.mapper.UserMapper;
import com.icheer.stock.system.userHistory.entity.UserHistory;
import com.icheer.stock.system.userHistory.mapper.UserHistoryMapper;
import com.icheer.stock.system.userHistory.service.UserHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class UserHistoryServiceImpl  extends ServiceImpl<UserHistoryMapper, UserHistory> implements UserHistoryService {
    private static final Logger log = LoggerFactory.getLogger(com.icheer.stock.system.userHistory.service.impl.UserHistoryServiceImpl.class);

     @Resource
     private StockInfoService stockInfoService;
     @Resource
     private UserMapper userMapper;

     @Resource
     private UserHistoryMapper userHistoryMapper;

    /**搜索记录 */
    public  void setSearchHistory(int userId, String code ){

        String log  = LocalDateTime.now().toString()+".USER."+String.valueOf(userId)+"._search_stock_code."+ code+stockInfoService.getOneByCode(code).getName();
        UserHistory userHistory = new UserHistory();
        userHistory.setCreateTime(LocalDateTime.now());
        userHistory.setUserId(userId);
        userHistory.setTsCode(code);
        userHistory.setRemark(log);
        userHistoryMapper.insert(userHistory);
        System.out.println(log);

    }

    /**搜索记录 */
    public  void setSearchNameHistory(int userId, String name ){
        String log  = LocalDateTime.now().toString()+".USER."+String.valueOf(userId)+"._search_stock_name."+"{"+name+"}" ;
        UserHistory userHistory = new UserHistory();
        userHistory.setCreateTime(LocalDateTime.now());
        userHistory.setUserId(userId);
        userHistory.setName(name);
        userHistory.setRemark(log);
        userHistoryMapper.insert(userHistory);
        System.out.println(log);
    }

    /**分析记录 */
    public  void setAnalysisHistory(int userId, String code ){
        String log  = LocalDateTime.now().toString()+".USER."+String.valueOf(userId)+"._analysis_stock_code."+ code+stockInfoService.getOneByCode(code).getName();
        UserHistory userHistory = new UserHistory();
        userHistory.setCreateTime(LocalDateTime.now());
        userHistory.setUserId(userId);
        userHistory.setTsCode(code);
        userHistory.setRemark(log);
        userHistoryMapper.insert(userHistory);
        System.out.println(log);
    }
}

