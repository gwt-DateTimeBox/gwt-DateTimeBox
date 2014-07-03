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

package com.github.gwt.user.client.ui.grids.days;

import com.github.gwt.user.client.ui.CalendarUtil;
import com.github.gwt.user.client.ui.grids.DateGridImpl;
import com.github.gwt.user.client.ui.grids.days.resources.DaysGridResources;
import com.github.gwt.user.client.ui.grids.days.resources.DaysGridResourcesImpl;

import java.util.Date;

/**
 * Grid of days of some month. Top left corner date is
 * the first day of the last week of the previous month.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class DaysGrid extends DateGridImpl {

    private static final DaysGridResources RESOURCES
            = DaysGridResourcesImpl.daysGridResources();
    private static final int DISPLAYED_WEEKS_OF_MONTH_COUNT = 6;

    static {
        RESOURCES.style().ensureInjected();
    }

    public DaysGrid() {
        this(new Date());
    }

    public DaysGrid(Date initialShownDate) {
        super(initialShownDate, RESOURCES);
        initGrid(DISPLAYED_WEEKS_OF_MONTH_COUNT, CalendarUtil.DAYS_IN_WEEK);
    }

    @Override
    protected String getText(int index) {
        return String.valueOf(getDate(index).getDate());
    }

    @Override
    protected Date getDate(int index) {
        // top left corner date is
        // the first day of the last week
        // of the previous month.
        Date topLeftCornerDate = CalendarUtil
                .getFirstDayOfLastWeekInPreviousMonth(getStateValue());
        CalendarUtil.addDaysToDate(topLeftCornerDate, index);
        return topLeftCornerDate;
    }

    @Override
    protected Cell getCell(Date date) {
        if (date == null || notOnGrid(date)) return null;

        Date topLeftCornerDate = CalendarUtil
                .getFirstDayOfLastWeekInPreviousMonth(getStateValue());
        int index = Math.abs(CalendarUtil
                .getDaysBetween(topLeftCornerDate, date));
        return getCell(index);
    }

    @Override
    protected boolean isFiller(int index) {
        return !CalendarUtil.isSameMonth(getStateValue(), getDate(index));
    }

    @Override
    protected boolean needsReload(Date oldStateValue, Date newStateValue) {
        return !CalendarUtil.isSameYear(newStateValue, oldStateValue)
                || !CalendarUtil.isSameMonth(oldStateValue, newStateValue);
    }

    //region Styles
    @Override
    protected String getCellStyle(int index) {

        if (CalendarUtil.isToday(getDate(index))) {
            return RESOURCES.style().isToday();
        }
        if (isHoliday(index)) {
            return RESOURCES.style().isHoliday();
        }
        if (CalendarUtil.isWeekend(getDate(index))) {
            return RESOURCES.style().isWeekend();
        }

        return RESOURCES.style().cell();
    }

    @Override
    protected String getHighlightedStyle(int index) {
        return (CalendarUtil.isWeekend(getDate(index))
                ? RESOURCES.style().isWeekendAndHighlighted()
                : RESOURCES.style().isHighlighted());
    }

    @Override
    protected String getSelectedStyle(int index) {

        // today takes precedence over all styles
        if (CalendarUtil.isToday(getDate(index))) {
            return RESOURCES.style().isTodayAndSelected();
        }

        return (CalendarUtil.isWeekend(getDate(index))
                ? RESOURCES.style().isWeekendAndSelected()
                : RESOURCES.style().isSelected());
    }

    @Override
    protected String getIsFillerStyle(int index) {
        return (CalendarUtil.isWeekend(getDate(index))
                ? RESOURCES.style().isWeekendAndFiller()
                : RESOURCES.style().isFiller());
    }

    @Override
    protected String getIsValueAndHighlightedStyle(int index) {
        return (CalendarUtil.isWeekend(getDate(index))
                ? RESOURCES.style().isWeekendValueAndHighlighted()
                : RESOURCES.style().isValueAndHighlighted());
    }

    @Override
    protected String getDisabledStyle(final int index) {
        return RESOURCES.style().isDisabled();
    }

    @Override
    protected String getIsValueStyle(int index) {
        return CalendarUtil.isToday(getDate(index))
                ? RESOURCES.style().isTodayAndValue() :
                (CalendarUtil.isWeekend(getDate(index))
                        ? RESOURCES.style().isWeekendValue()
                        : RESOURCES.style().isValue());
    }
    //endregion

    /**
     * @return previous month from {@link #getStateValue()}.
     */
    @Override
    protected Date prevStateValue() {
        Date stateValue = CalendarUtil.copyDate(getStateValue());
        int newMonth = stateValue.getMonth() - 1;
        while (newMonth < 0) {
            newMonth += 12;
            stateValue.setYear(stateValue.getYear() - 1);
        }
        stateValue.setMonth(newMonth);
        if (stateValue.getMonth() != newMonth) {
            /* If the date was October 31, for example, and
             * the month is set to June, then the new date will be treated as
             * if it were on July 1, because June has only 30 days. */
            stateValue.setDate(getStateValue().getDate() - stateValue.getDate());
            stateValue.setMonth(newMonth);
            /* last day of month was set forcibly */
        }
        return stateValue;
    }

    /**
     * @return next month from {@link #getStateValue()}.
     */
    @Override
    protected Date nextStateValue() {
        Date stateValue = CalendarUtil.copyDate(getStateValue());
        int newMonth = stateValue.getMonth() + 1;
        while (newMonth > 11) {
            newMonth -= 12;
            stateValue.setYear(stateValue.getYear() + 1);
        }
        stateValue.setMonth(newMonth);
        if (stateValue.getMonth() != newMonth) {
            /* If the date was October 31, for example, and
             * the month is set to June, then the new date will be treated as
             * if it were on July 1, because June has only 30 days. */
            stateValue.setDate(getStateValue().getDate() - stateValue.getDate());
            stateValue.setMonth(newMonth);
            /* last day of month was set forcibly */
        }
        return stateValue;
    }

    private boolean isHoliday(int index) {
        return false; // may: custom styles for holidays
    }
}

