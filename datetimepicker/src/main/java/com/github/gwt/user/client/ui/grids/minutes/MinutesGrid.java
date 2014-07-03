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
package com.github.gwt.user.client.ui.grids.minutes;

import com.github.gwt.user.client.ui.CalendarUtil;
import com.github.gwt.user.client.ui.grids.DateGridImpl;
import com.github.gwt.user.client.ui.grids.minutes.resources.MinutesGridResources;
import com.github.gwt.user.client.ui.grids.minutes.resources.MinutesGridResourcesImpl;
import com.google.gwt.i18n.client.NumberFormat;

import java.util.Date;

/**
 * Grid of 5-minutes intervals of some hour.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class MinutesGrid extends DateGridImpl {

    public static final int INTERVAL = 5;
    private static final MinutesGridResources RESOURCES
            = MinutesGridResourcesImpl.minutesGridResources();

    static {
        RESOURCES.style().ensureInjected();
    }

    public MinutesGrid() {
        this(new Date());
    }

    public MinutesGrid(final Date initialValue) {
        super(initialValue, RESOURCES);
        initGrid(3, 4); // 12 five-min intervals in an hour
    }

    @Override
    protected Date getDate(final int index) {
        Date date = CalendarUtil.copyDate(getStateValue());
        date.setMinutes(index * INTERVAL);
        return date;
    }

    @Override
    protected Cell getCell(final Date date) {
        if (date == null || notOnGrid(date)) return null;
        return getCell(date.getMinutes() / INTERVAL);
    }

    @Override
    protected String getText(final int index) {
        return getStateValue().getHours() + ":"
                + NumberFormat.getFormat("00").format(INTERVAL * index);
    }

    @Override
    protected boolean needsReload(Date oldStateValue, Date newStateValue) {
        return oldStateValue.getHours() != newStateValue.getHours();
    }

    /**
     * @return previous hour from {@link #getStateValue()}
     */
    @Override
    protected Date prevStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setHours(stVal.getHours() - 1);
        return stVal;
    }

    /**
     * @return next hour from {@link #getStateValue()}
     */
    @Override
    protected Date nextStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setHours(stVal.getHours() + 1);
        return stVal;
    }

}
