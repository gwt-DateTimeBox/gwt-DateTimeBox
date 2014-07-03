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

package com.github.gwt.user.client.ui.grids.years;

import com.github.gwt.user.client.ui.CalendarUtil;
import com.github.gwt.user.client.ui.grids.DateGridImpl;
import com.github.gwt.user.client.ui.grids.years.resources.YearsGridResources;
import com.github.gwt.user.client.ui.grids.years.resources.YearsGridResourcesImpl;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Represents grid of years of some decade.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class YearsGrid extends DateGridImpl {

    private static final YearsGridResources RESOURCES
            = YearsGridResourcesImpl.yearsGridResources();

    static {
        RESOURCES.style().ensureInjected();
    }

    private final DateTimeFormat YEARS_FORMAT
            = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR);

    public YearsGrid() {
        super(new Date(), RESOURCES);
        initGrid(CalendarUtil.YEARS_IN_DECADE);
    }

    @Override
    protected String getText(int index) {
        return YEARS_FORMAT.format(getDate(index));
    }

    @Override
    protected Cell getCell(Date date) {
        if (date == null || notOnGrid(date)) return null;
        int decadeYear = date.getYear()
                - CalendarUtil.getFirstYearInDecade(getStateValue());
        // +1 because first year is filler of previous decade
        int index = Math.abs(decadeYear + 1);
        return getCell(index);
    }

    @Override
    protected Date getDate(int index) {
        Date date = CalendarUtil.copyDate(getStateValue());
        date.setYear(CalendarUtil.getFirstYearInDecade(date) + index - 1);
        return date;
    }

    @Override
    protected boolean needsReload(Date oldStateValue, Date newStateValue) {
        return !CalendarUtil.isSameDecade(oldStateValue, newStateValue);
    }

    @Override
    protected boolean isFiller(int index) {
        return !CalendarUtil.isSameDecade(getDate(index), getStateValue());
    }

    /**
     * @return previous decade from {@link #getStateValue()}
     */
    @Override
    protected Date prevStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setYear(stVal.getYear() - CalendarUtil.YEARS_IN_DECADE);
        return stVal;
    }

    /**
     * @return next decade from {@link #getStateValue()}
     */
    @Override
    protected Date nextStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setYear(stVal.getYear() + CalendarUtil.YEARS_IN_DECADE);
        return stVal;
    }

}
