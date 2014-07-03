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

import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

/**
 * Specification of capabilities of {@link DateTimePicker}.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
interface DateTimePickerSpecification extends HasSelectionHandlers<Date>,
        HasCloseHandlers<Widget> {

    /**
     * Highlight value from data source.
     *
     * @param highlightValue if <code>true</code>, value from data source is
     *                       highlighted with isValue() style.
     *                       if <code>false</code>, value checking is disabled.
     */
    void setHighlightValue(boolean highlightValue);

    /**
     * Set one or two clicks are needed in order to drill down from top to
     * bottom panel.
     *
     * @param secondClickToDrill if <code>true</code>, second click is needed
     *                           in order to move from top panel (f.e. month)
     *                           to bottom (f.e. days).
     *                           if <code>false</code>, one click is needed
     *                           in order to move from top panel (f.e. month)
     *                           to bottom (f.e. days).
     */
    void setSecondClickToDrill(boolean secondClickToDrill);

    /**
     * Set appearance of decades grid. By default it is hidden.
     *
     * @param showDecadesGrid if <code>true</code>, century grid is visible.
     *                        if <code>false</code>, century grid is hidden.
     */
    void setShowDecadesGrid(boolean showDecadesGrid);

    /**
     * Set weekDayNamesFormat of week day names on days of month grid.
     * By default is DateTimeFormat.getFormat("ccc").
     *
     * @param weekDayNamesFormat of week day names.
     */
    void setWeekDayNamesFormat(DateTimeFormat weekDayNamesFormat);

    //region Header formats

    /**
     * Set format of header on hours view.
     *
     * @param hoursViewHeaderFormat format of header on hours view.
     */
    void setHoursViewHeaderFormat(DateTimeFormat hoursViewHeaderFormat);

    /**
     * Set format of header on days view.
     *
     * @param daysViewHeaderFormat format of header on days view.
     */
    public void setDaysViewHeaderFormat(final DateTimeFormat daysViewHeaderFormat);

    /**
     * Set format of header on months view.
     *
     * @param monthsViewHeaderFormat format of header on months view.
     */
    public void setMonthsViewHeaderFormat(final DateTimeFormat monthsViewHeaderFormat);

    //endregion

    /**
     * Set format of month names on months view.
     *
     * @param monthNamesFormat format of month names on month view.
     */
    void setMonthNamesFormat(DateTimeFormat monthNamesFormat);
}
