package com.icheer.stock.util;

import lombok.Data;

@Data
public class PostureResultUtils {
    private Long times;//次数
    private Long duration;//时长minus
    private String trend;//趋势 up/down
    private Float rate;
}
