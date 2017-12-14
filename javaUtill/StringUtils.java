package com.ailk.job.yuyueapi;

import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

public class StringUtils {

    public static String toString(Object obj) {
        return isNotNull(obj) ? String.valueOf(obj) : "";
    }

    public static boolean isNotNull(Object obj) {
        return obj != null ? true : false;
    }

    /**
     * 将日期以yyyyMMddHHmmss formate格式以String形式返回
     * @param dt
     * @return
     */
    public static String toString4LongTime(Date dt) {

        return isNotNull(dt) ? DateFormatUtils.format(dt, "yyyyMMddHHmmss") : null;
    }

    public static String toString(Object obj, String defStr) {
        return isNotNull(obj) ? String.valueOf(obj) : defStr;
    }

    /**
     * 非空
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        if (isNotNull(obj) && !"".equalsIgnoreCase(toString(obj))) {
            return true;
        }
        return false;
    }

    public static Integer toInteger(Object obj) {
        return isNotNull(obj) ? Integer.parseInt(toString(obj).trim()) : null;
    }

    public static Integer toInteger(Object obj, Integer defInt) {
        return isNotNull(obj) ? Integer.parseInt(toString(obj).trim()) : defInt;
    }

    public static Long toLong(Object obj) {
        return isNotEmpty(obj) ? Long.parseLong(toString(obj).trim()) : 0;
    }
}
