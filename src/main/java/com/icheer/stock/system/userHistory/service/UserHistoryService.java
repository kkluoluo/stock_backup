package com.icheer.stock.system.userHistory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.userHistory.entity.UserHistory;

public interface UserHistoryService extends IService<UserHistory> {


    /**搜索记录 */
    public  void setSearchHistory(int userId, String code );

    /**搜索记录 */
    public  void setSearchNameHistory(int userId, String name );

    /**分析记录 */
    public  void setAnalysisHistory(int userId, String code );

}
