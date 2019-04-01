package com.github.kyrotiko.vo;

import com.github.kyrotiko.truke.annotation.DateFormat;
import com.github.kyrotiko.truke.annotation.StringMappingFormat;
import com.github.kyrotiko.truke.annotation.MoneyFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author yuanyang(417168602 @ qq.com)
 * @date 2019/3/25 12:57
 */
@Data
@ToString
public class UserVO {

    @StringMappingFormat(mapping = "0:启用,1:未启用")
    private String status;

    @DateFormat
    private String birthday;

    @MoneyFormat(digit = 2)
    private String balance;


}
