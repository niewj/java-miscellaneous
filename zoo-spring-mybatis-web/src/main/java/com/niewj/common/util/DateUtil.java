
package com.niewj.common.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("all")
public final class DateUtil {

    public static Date addDays(String distance) {
        return DateUtils.addDays(new Date(), -Integer.valueOf(distance));
    }

    public static Date addHours(Integer hours) {
        return DateUtils.addHours(new Date(), hours);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        return DateUtils.isSameDay(date1, date2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return DateUtils.isSameDay(cal1, cal2);
    }

    public static boolean isSameInstant(Date date1, Date date2) {
        return DateUtils.isSameInstant(date1, date2);
    }

    public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
        return DateUtils.isSameInstant(cal1, cal2);
    }

    public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
        return DateUtils.isSameLocalTime(cal1, cal2);
    }

    public static Date parseDate(String str, String[] parsePatterns) throws ParseException {
        return DateUtils.parseDate(str, parsePatterns);
    }

    public static Date StringToDate(String mydate) {
        Date tempDate = new Date();
        if (mydate.equals(""))
            tempDate = null;
        else {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                tempDate = dateformat.parse(mydate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return tempDate;
    }

    public static Date round(Date date, int field) {
        return DateUtils.round(date, field);
    }

    public static Calendar round(Calendar date, int field) {
        return DateUtils.round(date, field);
    }

    public static Date round(Object date, int field) {
        return DateUtils.round(date, field);
    }

    public static Date truncate(Date date, int field) {
        return DateUtils.truncate(date, field);
    }

    public static Calendar truncate(Calendar date, int field) {
        return DateUtils.truncate(date, field);
    }

    public static Date truncate(Object date, int field) {
        return DateUtils.truncate(date, field);
    }

    public static Iterator iterator(Date focus, int rangeStyle) {
        return DateUtils.iterator(focus, rangeStyle);
    }

    public static Iterator iterator(Calendar focus, int rangeStyle) {
        return DateUtils.iterator(focus, rangeStyle);
    }

    public static Iterator iterator(Object focus, int rangeStyle) {
        return DateUtils.iterator(focus, rangeStyle);
    }

    public static String formatUTC(long millis, String pattern) {
        return DateFormatUtils.formatUTC(millis, pattern);
    }

    public static String formatUTC(Date date, String pattern) {
        return DateFormatUtils.formatUTC(date, pattern);
    }

    public static String formatUTC(long millis, String pattern, Locale locale) {
        return DateFormatUtils.formatUTC(millis, pattern, locale);
    }

    public static String formatUTC(Date date, String pattern, Locale locale) {
        return DateFormatUtils.formatUTC(date, pattern, locale);
    }

    public static String format(long millis, String pattern) {
        return DateFormatUtils.format(millis, pattern);
    }

    public static String format(Date date, String pattern) {
        if (date == null)
            return "";
        return DateFormatUtils.format(date, pattern);
    }

    public static String format(long millis, String pattern, TimeZone timeZone) {
        return DateFormatUtils.format(millis, pattern, timeZone);
    }

    public static String format(Date date, String pattern, TimeZone timeZone) {
        return DateFormatUtils.format(date, pattern, timeZone);
    }

    public static String format(long millis, String pattern, Locale locale) {
        return DateFormatUtils.format(millis, pattern, locale);
    }

    public static String format(Date date, String pattern, Locale locale) {
        return DateFormatUtils.format(date, pattern, locale);
    }

    public static String format(long millis, String pattern, TimeZone timeZone, Locale locale) {
        return DateFormatUtils.format(millis, pattern, timeZone, locale);
    }

    public static String format(Date date, String pattern, TimeZone timeZone, Locale locale) {
        return DateFormatUtils.format(date, pattern, timeZone, locale);
    }

    public static String format(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 把时间格式为 指定格式
     *
     * @param dateString
     * @param DataFormat
     * @return
     */
    public static Date parseDate(String dateString, String DataFormat) {
        SimpleDateFormat fordate = new SimpleDateFormat(DataFormat);
        if (dateString == null || dateString.equals(""))
            return null;
        try {
            return fordate.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前时间  且格式为：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static final String getCurrentTime() {
        long time = Calendar.getInstance().getTimeInMillis();
        return DateFormatUtils.format(time, "yyyy-MM-dd HH:mm:ss");
    }

    /*
     * 把日期格式化为  yyyy-MM-dd HH:mm:ss
     */
    public static final String getCurrentTime(Date d) {
        return DateFormatUtils.format(d, "yyyy-MM-dd HH:mm:ss");
    }

    /*
     * 获取当前时间为  ：yyyy-MM-dd 00:00:00
     */
    public static final String getCurrentDate() {
        long time = Calendar.getInstance().getTimeInMillis();
        return DateFormatUtils.format(time, "yyyy-MM-dd 00:00:00");
    }

    /**
     * 获取当前时间   格式为：yyyy-MM-dd 00:00:00
     *
     * @param d
     * @return
     */
    public static final String getCurrentDate(Date d) {
        return DateFormatUtils.format(d, "yyyy-MM-dd 00:00:00");
    }

    /**
     * 获取当前时间为格式为  yyyy-MM-dd
     *
     * @return
     */
    public static final String getCurrentDateShortStyle() {
        long time = Calendar.getInstance().getTimeInMillis();
        return DateFormatUtils.format(time, "yyyy-MM-dd");
    }

    /**
     * 把时间格式化为 yyyy-MM-dd
     *
     * @param d
     * @return
     */
    public static final String getCurrentDateShortStyle(Date d) {
        return DateFormatUtils.format(d, "yyyy-MM-dd");
    }

    /**
     * 把时间格式改为0000-00-00
     *
     * @param longStyleDate
     * @return
     */
    public static final String shortStyle(String longStyleDate) {
        if (longStyleDate == null || "".equals(longStyleDate))
            return "0000-00-00";
        return longStyleDate.substring(0, 10);
    }

    /**
     * 把时间格式改为0000-00-00 00:00:00
     *
     * @param shortStyleDate
     * @return
     */
    public static final String longStyle(String shortStyleDate) {
        if (shortStyleDate == null || "".equals(shortStyleDate))
            return "0000-00-00 00:00:00";
        return shortStyleDate + " 00:00:00";
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public static final String getCurrentYear() {
        long time = Calendar.getInstance().getTimeInMillis();
        return DateFormatUtils.format(time, "yyyy");
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static final String getCurrentMonth() {
        long time = Calendar.getInstance().getTimeInMillis();
        return DateFormatUtils.format(time, "MM");
    }

    /**
     * 获取当前的天
     *
     * @return
     */
    public static final String getCurrentDay() {
        long time = Calendar.getInstance().getTimeInMillis();
        return DateFormatUtils.format(time, "dd");
    }

    /**
     * 根据时间变量返回时间字符串
     *
     * @param pattern 时间字符串样式
     * @param date    时间变量
     * @return 返回时间字符串
     */
    public static String dateToString(Date date, String pattern) {

        if (date == null) {
            return null;
        }

        try {
            SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
            sfDate.setLenient(false);

            return sfDate.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * 返回当前时间
     *
     * @return 返回当前时间
     */
    public static Date getCurrentDateTime() {
        Calendar calNow = Calendar.getInstance();
        Date dtNow = calNow.getTime();

        return dtNow;
    }

    /**
     * 根据传入时间值 进行格式化 格式（**天**小时**分钟**秒）
     *
     * @param milliseconds
     * @return
     */
    public static String formatLongTime(long milliseconds) {
        StringBuffer result = new StringBuffer();
        long days = 0;
        long hours = 0;
        long minutes = 0;
        double seconds = milliseconds / 1000d;
        DecimalFormat df = new DecimalFormat("#.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (seconds >= 60) {
            minutes = (long) seconds / 60;
            seconds = seconds % 60;
        }
        seconds = Double.valueOf(df.format(seconds));
        if (minutes >= 60) {
            hours = minutes / 60;
            minutes = minutes % 60;
        }
        if (hours >= 24) {
            days = hours / 24;
            hours = hours % 24;
        }
        if (days > 0) {
            result.append(days + "天");
        }
        if (hours > 0 || days > 0) {
            result.append(hours + "小时");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            result.append(minutes + "分钟");
        }
        if (seconds > 0 || minutes > 0 || hours > 0 || days > 0) {
            result.append(seconds + "秒");
        }
        if ("".equals(result.toString())) {
            result.append("0秒");
        }
        return result.toString();
    }

    /**
     * 字符串改为时间
     *
     * @param date
     * @return
     */
    public static Date convertStr2Date(String date) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;
        try {
            time = formatDate.parse(date.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static Date converStr2SimpleDate(String date) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        Date time = null;
        try {
            time = formatDate.parse(date.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 根据相应的格式转换为日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date convertStr2Date(String date, String pattern) {
        SimpleDateFormat formatDate = new SimpleDateFormat(pattern);
        Date time = null;
        try {
            time = formatDate.parse(date.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 判断当前时间是否大于（指定时间+perid)
     *
     * @param dateTime 指定时间
     * @param period   有效时间，单位小时
     * @param calendar 维度
     * @return 如果大于：true，如果小于：false
     */
    public static boolean greaterThanTime(Date dateTime, Long period, int calendar) {
        GregorianCalendar now = new GregorianCalendar();
        Date currentTime = now.getTime();
        now.setTime(dateTime);
        now.add(calendar, period.intValue());
        return currentTime.after(now.getTime());
    }

    /**
     * 判断当前时间是否大于（指定时间+perid)
     *
     * @param dateTime 指定时间
     * @param period   有效时间，单位小时
     * @param calendar 维度
     * @return 如果大于：true，如果小于：false
     */
    public static boolean greaterThanTime(Date dateTime, int period, int calendar) {
        GregorianCalendar now = new GregorianCalendar();
        Date currentTime = now.getTime();
        now.setTime(dateTime);
        now.add(calendar, period);
        return currentTime.after(now.getTime());
    }

    public static boolean isInRegionTime(Date begin, Date end) {
        Date date = new Date();
        return date.before(end) && date.after(begin);
    }

    public static boolean before(Date source, Date targert) {
        if (source == null || targert == null)
            return false;
        return source.before(targert);
    }

    public static boolean after(Date source, Date targert) {
        if (source == null || targert == null)
            return false;
        return source.after(targert);
    }

    /**
     * 求指定日期的上月日期 yyyymm格式:
     * 举例： 如给定日期是201701的Date对象，则返回"201612"
     *
     * @param date 给定的日期
     * @return 给定日期的上月日期的字符串表示yyyymm
     */
    public static String getYyyyMmLastMonth(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");

        Calendar calendar = Calendar.getInstance();
        // 设置为当前时间
        calendar.setTime(date);
        // 设置为上一个月
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        date = calendar.getTime();

        return dateFormat.format(date);
    }
}