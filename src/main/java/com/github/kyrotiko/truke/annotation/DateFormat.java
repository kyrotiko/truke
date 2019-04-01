package com.github.kyrotiko.truke.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuanyang(417168602 @ qq.com)
 * @date 2019/3/25 13:22
 */

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD})
public @interface DateFormat {

    String pattern() default "yyyy-MM-dd HH:mm:ss";
}
