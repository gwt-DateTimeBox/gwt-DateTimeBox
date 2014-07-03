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
import com.github.gwt.user.client.ui.grids.days.resources.DaysOfWeekViewResources;
import com.github.gwt.user.client.ui.grids.days.resources.DaysOfWeekViewResourcesImpl;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents header with days of week names in horizontal panel.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class DaysOfWeekView extends Composite {

    private static final DaysOfWeekViewResources RESOURCES
            = DaysOfWeekViewResourcesImpl.daysOfWeekViewResources();

    private DateTimeFormat dayOfWeekFormat = DateTimeFormat.getFormat("ccc");
    private List<HasText> weekDayNameViews
            = new ArrayList<HasText>(CalendarUtil.DAYS_IN_WEEK);

    static {
        RESOURCES.style().ensureInjected();
    }

    public DaysOfWeekView() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setStyleName(RESOURCES.style().daysOfWeekView());
        // Set up the day labels.
        for (int i = 0; i < CalendarUtil.DAYS_IN_WEEK; i++) {
            Label label = new Label();
            panel.add(label);
            weekDayNameViews.add(label);

            int shift = CalendarUtil.getStartingDayOfWeek();
            int dayIdx = i +
                    shift < CalendarUtil.DAYS_IN_WEEK
                    ? i + shift
                    : i + shift - CalendarUtil.DAYS_IN_WEEK;

            if (CalendarUtil.isWeekend(dayIdx)) {
                label.setStyleName(RESOURCES.style().weekendLabel());
            } else {
                label.setStyleName(RESOURCES.style().weekdayLabel());
            }
        }
        initWidget(panel);
        setWeekDayNameLabelsText();
    }

    void setDayOfWeekFormat(final DateTimeFormat dayOfWeekFormat) {
        this.dayOfWeekFormat = dayOfWeekFormat;
        setWeekDayNameLabelsText();
    }

    private void setWeekDayNameLabelsText() {
        Date date = new Date();
        final String[] dayOfWeekNames = new String[CalendarUtil.DAYS_IN_WEEK];
        for (int i = 1; i <= CalendarUtil.DAYS_IN_WEEK; i++) {
            date.setDate(i);
            int dayOfWeek = date.getDay();
            dayOfWeekNames[dayOfWeek] = getDayOfWeekFormat().format(date);
        }

        for (int i = 0; i < CalendarUtil.DAYS_IN_WEEK; i++) {
            HasText hasText = weekDayNameViews.get(i);
            int shift = CalendarUtil.getStartingDayOfWeek();
            int dayIdx = i +
                    shift < CalendarUtil.DAYS_IN_WEEK
                    ? i + shift
                    : i + shift - CalendarUtil.DAYS_IN_WEEK;

            hasText.setText(dayOfWeekNames[dayIdx]);
        }
    }

    private DateTimeFormat getDayOfWeekFormat() {
        return dayOfWeekFormat;
    }

}