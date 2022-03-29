package com.icheer.stock.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
/**
 * com.eiker.common.utils
 *
 * @author eiker
 * @create 2020-06-19 16:45
 */
public class JSONUtil {
    /**
     * 将JSON字符串转为Java对象
     */
    public static <T> T toJavaObject(String result, Class<T> clazz) {
        return JSONObject.toJavaObject(JSONObject.parseObject(result), clazz);
    }

    /**
     * JSON字符串对象解析成java List对象
     */
    public static <T> List<T> toJavaList(String resultList, Class<T> clazz) {
        return JSONArray.parseArray(resultList).toJavaList(clazz);
    }
}
