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

package com.github.gwt.user.client.ui.grids.months;

import com.github.gwt.user.client.ui.CalendarUtil;
import com.github.gwt.user.client.ui.grids.DateGridImpl;
import com.github.gwt.user.client.ui.grids.months.resources.MonthsGridResources;
import com.github.gwt.user.client.ui.grids.months.resources.MonthsGridResourcesImpl;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Grid of months of some year.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class MonthsGrid extends DateGridImpl {

    private static final MonthsGridResources RESOURCES
            = MonthsGridResourcesImpl.monthsGridResources();

    static {
        RESOURCES.style().ensureInjected();
    }

    private final DateTimeFormat DEFAULT_MONTH_FORMAT
            = DateTimeFormat.getFormat(
            DateTimeFormat.PredefinedFormat.MONTH_ABBR);
    private Date monthNameProviderDate;
    private DateTimeFormat monthNameFormat;

    public MonthsGrid() {
        this(new Date());
    }

    public MonthsGrid(Date initialDate) {
        super(initialDate, RESOURCES);
        initGrid(CalendarUtil.MONTHS_IN_YEAR);
    }

    /**
     * Set format of month names on months view.
     *
     * @param monthNameFormat format of month names on month view.
     */
    public void setMonthNameFormat(final DateTimeFormat monthNameFormat) {
        this.monthNameFormat = monthNameFormat;
        reload();
    }

    @Override
    protected boolean needsReload(Date oldStateValue, Date newStateValue) {
        return !CalendarUtil.isSameYear(oldStateValue, newStateValue);
    }

    @Override
    protected Cell getCell(Date date) {
        if (date == null || notOnGrid(date)) return null;

        int index = Math.abs(date.getMonth());
        return getCell(index);
    }

    @Override
    protected String getText(int index) {
        return getMonthName(index);
    }

    @Override
    protected Date getDate(int index) {
        Date date = CalendarUtil.copyDate(getStateValue());

        date.setMonth(index);
        if (date.getMonth() != index) {
            /* If the date was October 31, for example, and
             * the month is set to June, then the new date will be treated as
             * if it were on July 1, because June has only 30 days. */
            date.setDate(getStateValue().getDate() - date.getDate());
            date.setMonth(index);
            /* last day of month was set forcibly, so June after October 31 became 30 June, Not 1st of July */
        }
        date.setYear(getStateValue().getYear());
        return date;
    }

    /**
     * @return previous year from current {@link #getStateValue()}.
     */
    @Override
    protected Date prevStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setYear(stVal.getYear() - 1);
        return stVal;
    }

    /**
     * @return next year from current {@link #getStateValue()}.
     */
    @Override
    protected Date nextStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setYear(stVal.getYear() + 1);
        return stVal;
    }

    private DateTimeFormat getMonthFormatter() {
        if (monthNameFormat == null) {
            monthNameFormat = DEFAULT_MONTH_FORMAT;
        }
        return monthNameFormat;
    }

    private String getMonthName(int monthNum) {
        if (monthNameProviderDate == null) {
            monthNameProviderDate = new Date();
        }
        monthNameProviderDate.setMonth(monthNum);
        return getMonthFormatter().format(monthNameProviderDate);
    }
}
