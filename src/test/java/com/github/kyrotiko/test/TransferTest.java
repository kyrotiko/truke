package com.github.kyrotiko.test;

import com.github.kyrotiko.pojo.User;
import com.github.kyrotiko.truke.core.FormatTransfer;
import com.github.kyrotiko.vo.UserVO;
import org.junit.Test;

import java.util.Date;

/**
 * @author yuanyang(417168602 @ qq.com)
 * @date 2019/3/25 14:01
 */
public class TransferTest {


    @Test
    public void testMoneyFormat() {
        User user = new User();
        user.setBalance(15660);
        user.setBirthday(new Date());
        user.setStatus(1);
        UserVO userVO = FormatTransfer.transfer(user, UserVO.class);
        System.out.println(userVO);
    }
}
