package com.github.kyrotiko.truke.core;

import com.github.kyrotiko.truke.annotation.DateFormat;
import com.github.kyrotiko.truke.annotation.MoneyFormat;
import com.github.kyrotiko.truke.annotation.StringMappingFormat;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yuanyang(417168602 @ qq.com)
 * @date 2019/3/25 11:45
 */
public class FormatTransfer {

    private static Map<Field, Map<String, String>> StringMappingFieldMap = new HashMap<Field, Map<String, String>>();
    private static Map<Field, SimpleDateFormat> fieldSimpleDateFormatMap = new HashMap<Field, SimpleDateFormat>(16);
    private static Map<Field, BigDecimal> fieldBigDecimalMap = new HashMap<Field, BigDecimal>(16);

    public static <T> List<T> transferList(List sourceList, Class<T> targetClass) {
        List<T> targetList = new ArrayList<T>(16);
        for (Object obj : sourceList) {
            targetList.add(transfer(obj, targetClass));
        }
        return targetList;
    }


    public static <T> T transfer(Object source, Class<T> targetClass) {
        T target = null;
        try {
            target = targetClass.newInstance();
            Field[] fields = targetClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String fieldName = field.getName();
                Field sourcePropertyField = source.getClass().getDeclaredField(fieldName);
                if (sourcePropertyField != null) {
                    sourcePropertyField.setAccessible(true);
                    Object sourceValue = sourcePropertyField.get(source);
                    if (sourceValue != null) {
                        Object targetValue = sourceValue;
                        StringMappingFormat smf = field.getAnnotation(StringMappingFormat.class);
                        MoneyFormat mf = field.getAnnotation(MoneyFormat.class);
                        DateFormat df = field.getAnnotation(DateFormat.class);
                        if (smf != null) {
                            Map<String, String> valueMap = null;
                            if (!StringMappingFieldMap.containsKey(field)) {
                                String mapping = smf.mapping();
                                valueMap = new HashMap<String, String>(16);
                                String[] kvs = mapping.split(",");
                                for (String kv : kvs) {
                                    String[] tmp = kv.split(":");
                                    valueMap.put(tmp[0], tmp[1]);
                                }
                                StringMappingFieldMap.put(field, valueMap);
                            } else {
                                valueMap = StringMappingFieldMap.get(field);
                            }
                            targetValue = valueMap.get(sourceValue.toString());
                        } else if (mf != null) {
                            Integer digit = mf.digit();
                            Integer basic = mf.basic();
                            Long value = Long.valueOf(String.valueOf(sourceValue)).longValue();
                            BigDecimal b1 = new BigDecimal(value);
                            BigDecimal b2 = null;
                            if (fieldBigDecimalMap.containsKey(field)) {
                                b2 = fieldBigDecimalMap.get(field);
                            } else {
                                b2 = new BigDecimal(basic);
                                fieldBigDecimalMap.put(field, b2);
                            }
                            BigDecimal b3 = b1.divide(b2, digit, BigDecimal.ROUND_HALF_UP);
                            targetValue = b3.toString();
                        } else if (df != null) {
                            SimpleDateFormat sdf = null;
                            if (!fieldSimpleDateFormatMap.containsKey(field)) {
                                String pattern = df.pattern();
                                sdf = new SimpleDateFormat(pattern);
                                fieldSimpleDateFormatMap.put(field, sdf);
                            } else {
                                sdf = fieldSimpleDateFormatMap.get(field);
                            }
                            Date date = parseDate(sourceValue);
                            if (date == null) {
                                targetValue = null;
                            } else {
                                targetValue = sdf.format(date);
                            }
                        }
                        field.setAccessible(true);
                        field.set(target, targetValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return target;
        }
    }


    private static Date parseDate(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Date) {
            return (Date) o;
        }
        if (o instanceof java.sql.Date) {
            return (Date) o;
        }
        if (o instanceof String) {
            // yyyy-MM-dd HH:mm:ss  /
            String d = (String) o;
            StringBuilder format = new StringBuilder("yyyy");
            if (d.charAt(4) == '-') {
                format.append("-MM-dd");
            } else if (d.charAt(4) == '/') {
                format.append("/MM/dd");
            } else if (d.charAt(4) == '_') {
                format.append("_MM_dd");
            } else {
                format.append("MMdd");
            }
            if (d.length() < format.length()) {
                return null;
            } else if (d.length() == format.length()) {
                return parseDate(d, format.toString());
            }
            if (d.charAt(format.length()) == ' ') {
                format.append(' ');
            }
            if (d.charAt(format.length() + 2) == ':') {
                format.append("HH:mm:ss");
            } else if (d.charAt(format.length() + 2) == '/') {
                format.append("HH/mm/ss");
            } else {
                format.append("HHmmss");
            }
            if (d.length() < format.length()) {
                return null;
            }
            if (d.length() == format.length()) {
                return parseDate(d, format.toString());
            }
            if (d.charAt(format.length()) == '.' && d.length() == (format.length() + 4)) {
                format.append(".SSS");
            } else if (d.length() == (format.length() + 3)) {
                format.append("SSS");
            } else {
                d = d.substring(0, format.length());
            }
            return parseDate(d, format.toString());
        }
        if (o instanceof Long) {
            long l = (Long) o;
            if (l < 10000000000L) {
                return new Date(l * 1000);
            }
            return new Date(l);
        }
        if (o instanceof Integer) {
            long l = (Integer) o * 1000;
            return new Date(l);
        }
        return null;
    }

    private static Date parseDate(String d, String format) {
        try {
            return new SimpleDateFormat(format).parse(d);
        } catch (ParseException e) {
            return null;
        }
    }
}
