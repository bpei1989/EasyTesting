package com.easytesting.util;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 时间和日期工具类
 */
public final class TimeUtil {
	
	private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();

	private static final Object object = new Object();
	
    public static final String DEFAULT_FORMAT_STYLE = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_FORMAT_STYLE_NOSPACE = "yyyy-MM-dd_HH:mm:ss";
    public static final String DEFAULT_MINUTE_STYLE = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_MINUTE_STYLE_NOSPACE = "yyyy-MM-dd_HH:mm";
    public static final String DEFAULT_HOUR_STYLE = "yyyy-MM-dd HH";
    public static final String DEFAULT_HOUR_STYLE_NOSPACE = "yyyy-MM-dd_HH";
    public static final String DEFAULT_HOUR_STYLE_NOYEAR = "MM-dd HH:mm";
    public static final String DEFAULT_ONLYHOUR_STYLE = "HH:mm";
    public static final String DEFAULT_DAY_STYLE = "yyyy-MM-dd";
    public static final String DEFAULT_DAY_STYLE_NOYEAR = "MM-dd";
    public static final String DEFAULT_DAY_STYLE_NOSPACE = "yyyyMMdd";
    public static final String DEFAULT_MILLISECOND_STYLE = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String DEFAULT_MILLISECOND_STYLE_1 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DEFAULT_ONLYSEC_STYLE = "mm:ss";
    public static final String DEFAULT_ONLY_HOUR_STYLE = "HH";

    public static final long SECOND = 1000L;
    public static final long MINUTE = 1000 * 60L;
    public static final long HOUR = 1000 * 60 * 60L;
    public static final long DAY = 1000 * 60 * 60 * 24L;

    public static final String BEGIN_DATE = "beginDate";
    public static final String END_DATE = "endDate";
    
	/**
	 * 获取SimpleDateFormat，默认yyyy-MM-dd HH:mm:ss
	 * 
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	private static SimpleDateFormat getDateFormat()
			throws RuntimeException {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(DEFAULT_FORMAT_STYLE);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(DEFAULT_FORMAT_STYLE);
		return dateFormat;
	}
    
	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param pattern
	 *            日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	private static SimpleDateFormat getDateFormat(String pattern)
			throws RuntimeException {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return 日期
	 */
	public static Date String2Date(String date, String pattern) {
		Date myDate = null;
		if (date != null) {
			try {
				myDate = getDateFormat(pattern).parse(date);
			} catch (Exception e) {
			}
		}
		return myDate;
	}
	
	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * 
	 * @param date
	 *            日期=
	 * @param pattern
	 *            日期格式
	 * @return 日期字符串
	 */
    public static String Date2String(Date date, String formatStyle) {
		String myDate = null;
		if (date != null) {
			try {
				myDate = getDateFormat(formatStyle).format(date);
			} catch (Exception e) {
			}
		}
		return myDate;
    }

    public static Date format2Hour(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() / HOUR * HOUR);
    }

    public static Date format2Min(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() / MINUTE * MINUTE);
    }

    public static Date format2Second(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() / SECOND * SECOND);
    }

    @SuppressWarnings("deprecation")
    public static Date format2Day(Date d) {
        if (d.getHours() >= 8) {
            return new Date(d.getTime() / DAY * DAY - (8 * HOUR));
        } else {
            return new Date(d.getTime() / HOUR * HOUR - HOUR * d.getHours());
        }
    }

    public static Date after(int times, Date date, TimeUnit timeUnit) {
        long d = date.getTime() / 1000;

        if (timeUnit == SECONDS) {
            return new Date((d + times) * 1000);
        } else if (timeUnit == MINUTES) {
            return new Date((d + times * 60) * 1000);
        } else if (timeUnit == HOURS) {
            return new Date((d + times * 60 * 60) * 1000);
        } else if (timeUnit == DAYS) {
            return new Date((d + times * 60 * 60 * 24) * 1000);
        }
        throw new RuntimeException(
                "only support TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS !");
    }

    public static Date before(int times, Date date, TimeUnit timeUnit) {
        long d = date.getTime() / 1000;

        if (timeUnit == SECONDS) {
            return new Date((d - times) * 1000);
        } else if (timeUnit == MINUTES) {
            return new Date((d - times * 60) * 1000);
        } else if (timeUnit == HOURS) {
            return new Date((d - times * 60 * 60) * 1000);
        } else if (timeUnit == DAYS) {
            return new Date((d - times * 60 * 60 * 24) * 1000);
        }

        throw new RuntimeException(
                "only support TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS!");
    }


    /**
     * 取得两个时间段的时间间隔
     *
     * @param d1 时间1
     * @param d2 时间2
     * @return t2 与t1的间隔天数
     * @throws ParseException 如果输入的日期格式不是0000-00-00 格式抛出异常
     * @throws ParseException
     * @author color
     */
    public static int getDaysBetween(Date d1, Date d2)
            throws ParseException {
        int betweenDays = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        // 保证第二个时间一定大于第一个时间
        if (c1.after(c2)) {
            c1 = c2;
            c2.setTime(d1);
        }
        int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        betweenDays = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < betweenYears; i++) {
            c1.set(Calendar.YEAR, (c1.get(Calendar.YEAR) + 1));
            betweenDays += c1.getMaximum(Calendar.DAY_OF_YEAR);
        }
        return betweenDays;
    }

    /**
     * 按指定日期单位计算两个日期间的间隔
     *
     * @param timeInterval， 间隔单位，如year
     * @param d1 时间字符串
     * @param d2 时间字符串
     * @return 间隔
     */
    public static long dateDiff(String timeInterval, String d1, String d2) throws ParseException {

        Date date1 = TimeUtil.String2Date(d1, DEFAULT_DAY_STYLE_NOSPACE);
        Date date2 = TimeUtil.String2Date(d2, DEFAULT_DAY_STYLE_NOSPACE);

        if (timeInterval.equals("year")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            int time = calendar.get(Calendar.YEAR);
            calendar.setTime(date2);
            return time - calendar.get(Calendar.YEAR);
        }

        if (timeInterval.equals("quarter")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            int time = calendar.get(Calendar.YEAR) * 4;
            calendar.setTime(date2);
            time -= calendar.get(Calendar.YEAR) * 4;
            calendar.setTime(date1);
            time += calendar.get(Calendar.MONTH) / 4;
            calendar.setTime(date2);
            return time - calendar.get(Calendar.MONTH) / 4;
        }

        if (timeInterval.equals("month")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            int time = calendar.get(Calendar.YEAR) * 12;
            calendar.setTime(date2);
            time -= calendar.get(Calendar.YEAR) * 12;
            calendar.setTime(date1);
            time += calendar.get(Calendar.MONTH);
            calendar.setTime(date2);
            return time - calendar.get(Calendar.MONTH);
        }

        if (timeInterval.equals("week")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            int time = calendar.get(Calendar.YEAR) * 52;
            calendar.setTime(date2);
            time -= calendar.get(Calendar.YEAR) * 52;
            calendar.setTime(date1);
            time += calendar.get(Calendar.WEEK_OF_YEAR);
            calendar.setTime(date2);
            return time - calendar.get(Calendar.WEEK_OF_YEAR);
        }

        if (timeInterval.equals("day")) {
            long time = date1.getTime() / 1000 / 60 / 60 / 24;
            return time - date2.getTime() / 1000 / 60 / 60 / 24;
        }

        if (timeInterval.equals("hour")) {
            long time = date1.getTime() / 1000 / 60 / 60;
            return time - date2.getTime() / 1000 / 60 / 60;
        }

        if (timeInterval.equals("minute")) {
            long time = date1.getTime() / 1000 / 60;
            return time - date2.getTime() / 1000 / 60;
        }

        if (timeInterval.equals("second")) {
            long time = date1.getTime() / 1000;
            return time - date2.getTime() / 1000;
        }

        return date1.getTime() - date2.getTime();
    }

    public static int diffHourWithNow(Date d1) {
        return diffHour(d1, new Date());
    }

    public static int diffHour(Date d1, Date now) {
        DateTime d = new DateTime(d1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime _now = new DateTime(now).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);

        return (int) ((d.getMillis() - _now.getMillis()) / 1000 / 60 / 60);
    }

    /**
     * 判断一个字符串是否是 数字(正负\小数)
     *
     * @return
     */
    public static boolean isNumber(String str) {
        Boolean strResult = str.matches("-?[0-9]+.*[0-9]*");
        if (strResult == true) {
            return true;
        } else {
            return false;
        }
    }

}
