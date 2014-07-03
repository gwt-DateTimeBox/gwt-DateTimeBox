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
package com.github.gwt.user.client.ui.grids.hours;


import com.github.gwt.user.client.ui.CalendarUtil;
import com.github.gwt.user.client.ui.grids.DateGridImpl;
import com.github.gwt.user.client.ui.grids.hours.resources.HoursGridResources;
import com.github.gwt.user.client.ui.grids.hours.resources.HoursGridResourcesImpl;

import java.util.Date;

/**
 * Grid of hours (24 format) of some day.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class HoursGrid extends DateGridImpl {

    private static final HoursGridResources RESOURCES
            = HoursGridResourcesImpl.hoursGridResources();

    static {
        RESOURCES.style().ensureInjected();
    }

    public HoursGrid() {
        this(new Date());
    }

    public HoursGrid(final Date initialValue) {
        super(initialValue, RESOURCES);
        initGrid(6, 4);
    }

    @Override
    protected Date getDate(final int index) {
        Date date = CalendarUtil.copyDate(getStateValue());
        date.setHours(index);
        return date;
    }

    @Override
    protected Cell getCell(final Date date) {
        if (date == null || notOnGrid(date)) return null;
        return getCell(date.getHours());
    }

    @Override
    protected String getText(final int index) {
        return String.valueOf(index) + ":00";
    }

    /**
     * @return previous day from {@link #getStateValue()}
     */
    @Override
    protected Date prevStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setDate(stVal.getDate() - 1);
        return stVal;
    }

    /**
     * @return next day from {@link #getStateValue()}
     */
    @Override
    protected Date nextStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setDate(stVal.getDate() + 1);
        return stVal;
    }
}
