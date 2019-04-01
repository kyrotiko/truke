package com.github.kyrotiko.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author yuanyang(417168602 @ qq.com)
 * @date 2019/3/25 12:56
 */
@Data
public class User {

    private Integer status;

    private Date birthday;

    private Integer balance;

}
