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

package com.github.gwt.user.client.ui.grids.decades;

import com.github.gwt.user.client.ui.CalendarUtil;
import com.github.gwt.user.client.ui.grids.DateGridImpl;
import com.github.gwt.user.client.ui.grids.decades.resources.DecadesGridResources;
import com.github.gwt.user.client.ui.grids.decades.resources.DecadesGridResourcesImpl;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Grid of decades of some century.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class DecadesGrid extends DateGridImpl {

    private static final String DECADES_CELL_TEXT_SEPARATOR = " - \n";
    private static final DecadesGridResources RESOURCES
            = DecadesGridResourcesImpl.decadesGridResources();

    static {
        RESOURCES.style().ensureInjected();
    }

    private final DateTimeFormat YEARS_FORMAT
            = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR);

    public DecadesGrid() {
        super(new Date(), RESOURCES);
        initGrid(CalendarUtil.DECADES_IN_CENTURY);
    }

    @Override
    protected String getText(int index) {
        Date date = getDate(index);
        String decadeStart = YEARS_FORMAT.format(date);
        date.setYear(date.getYear() + CalendarUtil.YEARS_IN_DECADE - 1);
        String decadeEnd = YEARS_FORMAT.format(date);
        return decadeStart + DECADES_CELL_TEXT_SEPARATOR + decadeEnd;
    }

    @Override
    protected Date getDate(int index) {
        Date date = CalendarUtil.copyDate(getStateValue());

        // (index - 1) because of filler cell from previous century
        date.setYear(CalendarUtil.getFirstYearInCentury(date)
                + (index - 1) * CalendarUtil.YEARS_IN_DECADE);
        return date;
    }

    @Override
    protected boolean isFiller(int index) {
        return !CalendarUtil.isSameCentury(getDate(index), getStateValue());
    }

    @Override
    protected Cell getCell(Date date) {
        if (date == null || notOnGrid(date)) return null;

        int yearOfCentury = date.getYear()
                - CalendarUtil.getFirstYearInCentury(getStateValue());

        // + 1 because first cell is filler from previous century
        int index = Math.abs((yearOfCentury / CalendarUtil.YEARS_IN_DECADE) + 1);
        return getCell(index);
    }

    @Override
    protected boolean needsReload(Date oldStateValue, Date newStateValue) {
        return !CalendarUtil.isSameCentury(oldStateValue, newStateValue);
    }

    /**
     * @return previous century from {@link #getStateValue()}
     */
    @Override
    protected Date prevStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setYear(stVal.getYear() - CalendarUtil.YEARS_IN_CENTURY);
        return stVal;
    }

    /**
     * @return next century from {@link #getStateValue()}
     */
    @Override
    protected Date nextStateValue() {
        Date stVal = CalendarUtil.copyDate(getStateValue());
        stVal.setYear(stVal.getYear() + CalendarUtil.YEARS_IN_CENTURY);
        return stVal;
    }
}