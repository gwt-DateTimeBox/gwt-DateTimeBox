/*
 * Copyright 2014 Rinat Enikeev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gwt.user.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormatInfo;
import com.google.gwt.i18n.client.LocaleInfo;

import java.util.Date;

/**
 * Helper date methods. Some of them are from
 * {@link com.google.gwt.user.datepicker.client.CalendarUtil}.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class CalendarUtil {
    public static final int DECADES_IN_CENTURY = 10;
    public static final int YEARS_IN_DECADE = 10;
    public static final int YEARS_IN_CENTURY = 100;
    public static final int DAYS_IN_WEEK = 7;
    public static final int MONTHS_IN_YEAR = 12;

    private static int startingDay;
    private static int firstDayOfWeekend;
    private static int lastDayOfWeekend;

    static {
        if (GWT.isClient()) {
            DateTimeFormatInfo dateTimeFormatInfo = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo();
            // Finding the start and end of weekend
            firstDayOfWeekend = dateTimeFormatInfo.weekendStart();
            lastDayOfWeekend = dateTimeFormatInfo.weekendEnd();
            startingDay = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().firstDayOfTheWeek();
        }
    }

    public static int getFirstYearInCentury(Date date) {
        int year = date.getYear();
        int yearsInCentury = YEARS_IN_DECADE * DECADES_IN_CENTURY;

        // java.util.Date year is less than zero before 1900.
        int yearOfCentury;
        if (year < 0) {
            yearOfCentury = (yearsInCentury + (year % yearsInCentury)) % yearsInCentury;
        } else {
            yearOfCentury = year % yearsInCentury;
        }
        return year - yearOfCentury;
    }

    public static Date copyDate(Date date) {
        if (date == null) {
            return null;
        }
        Date newDate = new Date();
        newDate.setTime(date.getTime());
        return newDate;
    }

    public static int getFirstYearInDecade(Date date) {
        int year = date.getYear();
        int yearOfDecade;
        if (year < 0) {
            yearOfDecade = ((year % YEARS_IN_DECADE) + YEARS_IN_DECADE)
                    % YEARS_IN_DECADE;
        } else {
            yearOfDecade = (year % YEARS_IN_DECADE);
        }
        return year - yearOfDecade;
    }

    /**
     * Check if two dates represent the same date of the same year, even if they
     * have different times.
     *
     * @param date0 a date
     * @param date1 a second date
     * @return true if the dates are the same
     */
    @SuppressWarnings("deprecation") // GWT requires Date
    public static boolean isSameDate(Date date0, Date date1) {
        assert date0 != null : "date0 cannot be null";
        assert date1 != null : "date1 cannot be null";
        return date0.getYear() == date1.getYear()
                && date0.getMonth() == date1.getMonth()
                && date0.getDate() == date1.getDate();
    }

    public static void addDaysToDate(Date date, int days) {
        date.setDate(date.getDate() + days);
    }

    /**
     * Returns the number of days between the two dates. Time is ignored.
     *
     * @param start  starting date
     * @param finish ending date
     * @return the different
     */
    public static int getDaysBetween(Date start, Date finish) {
        // Convert the dates to the same time
        start = copyDate(start);
        resetTime(start);
        finish = copyDate(finish);
        resetTime(finish);

        long aTime = start.getTime();
        long bTime = finish.getTime();

        long adjust = 60 * 60 * 1000;
        adjust = (bTime > aTime) ? adjust : -adjust;

        return (int) ((bTime - aTime + adjust) / (24 * 60 * 60 * 1000));
    }

    /**
     * Resets the date to have no time modifiers. Note that the hour might not be zero if the time
     * hits a DST transition date.
     *
     * @param date the date
     */
    @SuppressWarnings("deprecation") // GWT requires Date
    public static void resetTime(Date date) {
        long msec = date.getTime();
        msec = (msec / 1000) * 1000;
        date.setTime(msec);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
    }

    public static boolean isSameCentury(final Date date1, final Date date2) {
        boolean firstDateIsInLessCentury
                = date1.getYear() < CalendarUtil.getFirstYearInCentury(date2);

        boolean firstDateIsInGreaterCentury
                = date1.getYear() >= (CalendarUtil.getFirstYearInCentury(date2)
                + CalendarUtil.YEARS_IN_CENTURY);

        return !firstDateIsInLessCentury && !firstDateIsInGreaterCentury;
    }

    public static boolean isSameMonth(final Date date1, final Date date2) {
        return date1.getMonth() == date2.getMonth();
    }

    public static boolean isWeekend(Date date) {
        int dayOfWeek = date.getDay();
        return dayOfWeek == firstDayOfWeekend || dayOfWeek == lastDayOfWeekend;
    }

    public static boolean isWeekend(int dayOfWeek) {
        return dayOfWeek == firstDayOfWeekend || dayOfWeek == lastDayOfWeekend;
    }

    /**
     * Returns the day of the week on which week starts in the current locale. The
     * range between 0 for Sunday and 6 for Saturday.
     *
     * @return the day of the week
     */
    public static int getStartingDayOfWeek() {
        return startingDay;
    }


    public static boolean isToday(Date date) {
        return CalendarUtil.isSameDate(date, new Date());
    }

    public static boolean isSameYear(final Date date1, final Date date2) {
        return date1.getYear() == date2.getYear();
    }

    public static Date getFirstDayOfLastWeekInPreviousMonth(Date ofDate) {
        Date firstDayOnGrid = getFirstDayOfFirstWeekInCurrentMonth(ofDate);
        if (firstDayOnGrid.getDate() == 1) {
            // show one empty week if date is Monday is the first in month.
            CalendarUtil.addDaysToDate(firstDayOnGrid,
                    -CalendarUtil.DAYS_IN_WEEK);
            return firstDayOnGrid;
        } else {
            return firstDayOnGrid;
        }
    }

    public static boolean isSameDecade(Date date1, Date date2) {
        int year1 = date1.getYear() + 1900; // to avoid negative years
        int year2 = date2.getYear() + 1900;
        return (year1 - (year1 % CalendarUtil.YEARS_IN_DECADE))
                == (year2 - (year2 % CalendarUtil.YEARS_IN_DECADE));
    }

    private static Date getFirstDayOfFirstWeekInCurrentMonth(Date ofDate) {
        Date firstDayOfMonth = copyDate(ofDate);
        firstDayOfMonth.setDate(1); // first day of month
        int wkDayNumOfMonth1stDay = firstDayOfMonth.getDay();
        int weekStartDayNum = CalendarUtil.getStartingDayOfWeek();
        if (wkDayNumOfMonth1stDay == weekStartDayNum) {
            return firstDayOfMonth;
        } else {
            int offset = wkDayNumOfMonth1stDay - weekStartDayNum > 0 ? wkDayNumOfMonth1stDay - weekStartDayNum
                    : CalendarUtil.DAYS_IN_WEEK - (weekStartDayNum - wkDayNumOfMonth1stDay);
            CalendarUtil.addDaysToDate(firstDayOfMonth, -offset);
            return firstDayOfMonth;
        }
    }
}
