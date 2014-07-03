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

import com.github.gwt.user.client.ui.events.*;
import com.github.gwt.user.client.ui.events.appear.did.DidAppearEvent;
import com.github.gwt.user.client.ui.events.appear.did.DidAppearHandler;
import com.github.gwt.user.client.ui.events.appear.will.WillAppearEvent;
import com.github.gwt.user.client.ui.events.appear.will.WillAppearHandler;
import com.github.gwt.user.client.ui.grids.days.DaysView;
import com.github.gwt.user.client.ui.grids.decades.DecadesGrid;
import com.github.gwt.user.client.ui.grids.hours.HoursGrid;
import com.github.gwt.user.client.ui.grids.minutes.MinutesGrid;
import com.github.gwt.user.client.ui.grids.months.MonthsGrid;
import com.github.gwt.user.client.ui.grids.years.YearsGrid;
import com.github.gwt.user.client.ui.resources.DateTimePickerResources;
import com.github.gwt.user.client.ui.resources.DateTimePickerResourcesImpl;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Allows to pick date.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class DateTimePicker extends Composite
        implements DateTimePickerSpecification,
        ValueChain<Date>,
        HasStateValue<Date>,
        HasSelectedValue<Date> {

    private static final DateTimePickerResources RESOURCES
            = DateTimePickerResourcesImpl.dateTimePickerResources();

    //region Pager header formats
    private static final DateTimeFormat MINUTES_VIEW_HEADER_DEFAULT_FORMAT
            = DateTimeFormat.getFormat("dd MMMM yyyy");
    private static final DateTimeFormat HOURS_VIEW_HEADER_DEFAULT_FORMAT
            = DateTimeFormat.getFormat("dd MMMM yyyy");
    private static final DateTimeFormat DAYS_VIEW_HEADER_DEFAULT_FORMAT
            = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_ABBR);
    private static final DateTimeFormat MONTHS_VIEW_HEADER_DEFAULT_FORMAT
            = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR);
    //region Year and Decade views headers
    private static final DateTimeFormat YEARS_DEFAULT_FORMAT
            = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR);

    private static final String YEARS_HEADER_SEPARATOR = " - ";
    private static final String DECADES_HEADER_SEPARATOR = " - ";
    //endregion

    //endregion

    static {
        RESOURCES.style().ensureInjected();
    }

    private final HasValue<Date> dataSource;
    private final Date initialDate;

    private Date shownDate;
    private Date selectedDate;

    private boolean secondClickToDrill;
    private HandlerRegistration yearsShiftUpHandlerRegistration;
    private HandlerRegistration backNextCtrlArrowHandler;
    private HandlerRegistration dataSourceValueChange;

    LayerPanel layerPanel;

    DateTimePickerPanel hoursPanel;
    DateTimePickerPanel daysPanel;
    DateTimePickerPanel monthsPanel;
    DateTimePickerPanel yearsPanel;
    DateTimePickerPanel decadesPanel;

    DaysView daysView;
    MonthsGrid monthsGrid;
    DateTimePickerPanel minutesPanel;

    private final List<DateTimePickerPanel> datePanels = new ArrayList<DateTimePickerPanel>(5);

    //region Header formats
    private DateTimeFormat hoursViewHeaderFormat = HOURS_VIEW_HEADER_DEFAULT_FORMAT;
    private DateTimeFormat daysViewHeaderFormat = DAYS_VIEW_HEADER_DEFAULT_FORMAT;
    private DateTimeFormat monthsViewHeaderFormat = MONTHS_VIEW_HEADER_DEFAULT_FORMAT;
    private DateTimeFormat minutesViewHeaderFormat = MINUTES_VIEW_HEADER_DEFAULT_FORMAT;
    //endregion

    //region Constructors
    public DateTimePicker(Date initialDate) {
        this(null, initialDate);
    }

    public DateTimePicker(HasValue<Date> dataSource) {
        this(dataSource, new Date());
    }

    public DateTimePicker(HasValue<Date> dataSource, Date initialDate) {
        this.dataSource = dataSource;
        this.initialDate = initialDate;

        layerPanel = new LayerPanel<Date>();
        minutesPanel = new DateTimePickerPanel(new Pager(), new MinutesGrid(initialDate));
        layerPanel.add(minutesPanel);

        hoursPanel = new DateTimePickerPanel(new Pager(), new HoursGrid(initialDate));
        layerPanel.add(hoursPanel);

        daysView = new DaysView(initialDate);
        daysPanel = new DateTimePickerPanel(new Pager(), daysView);
        layerPanel.add(daysPanel);

        monthsGrid = new MonthsGrid(initialDate);
        monthsPanel = new DateTimePickerPanel(new Pager(), monthsGrid);
        layerPanel.add(monthsPanel);

        yearsPanel = new DateTimePickerPanel(new Pager(), new YearsGrid());
        layerPanel.add(yearsPanel);

        decadesPanel = new DateTimePickerPanel(new Pager(), new DecadesGrid());
        layerPanel.add(decadesPanel);

        initWidget(layerPanel);
        layerPanel.setWidget(daysPanel); // initial view
        layerPanel.setStyleName(RESOURCES.style().datePicker());

        datePanels.add(minutesPanel);
        datePanels.add(hoursPanel);
        datePanels.add(daysPanel);
        datePanels.add(monthsPanel);
        datePanels.add(yearsPanel);
        datePanels.add(decadesPanel);

        minutesPanel.setFormatStrategy(new FormatDateStrategy() {
            @Override
            public String format(final Date date) {
                return minutesViewHeaderFormat.format(date);
            }
        });

        hoursPanel.setFormatStrategy(new FormatDateStrategy() {

            @Override
            public String format(final Date date) {
                return hoursViewHeaderFormat.format(date);
            }
        });
        daysPanel.setFormatStrategy(new FormatDateStrategy() {

            @Override
            public String format(final Date date) {
                return daysViewHeaderFormat.format(date);
            }
        });
        monthsPanel.setFormatStrategy(new FormatDateStrategy() {

            @Override
            public String format(final Date date) {
                return monthsViewHeaderFormat.format(date);
            }
        });
        yearsPanel.setFormatStrategy(new FormatDateStrategy() {

            @Override
            public String format(final Date date) {
                return getDecadeString(date);
            }
        });
        decadesPanel.setFormatStrategy(new FormatDateStrategy() {

            @Override
            public String format(final Date date) {
                return getCenturyString(date);
            }
        });

        for (DateTimePickerPanel panel : datePanels) {
            panel.setStateValue(getInitialDate());
        }

        syncLayersShownDate();
        delegateShiftDrill();
        bindSelectionAndCloseEvents();
        bindCloseOnSelectMinute();

        setSecondClickToDrill(true);
        setHighlightValue(true);
    }

    //region Specification methods

    /**
     * Set format of header on hours view.
     *
     * @param hoursViewHeaderFormat format of header on hours view.
     */
    public void setHoursViewHeaderFormat(final DateTimeFormat hoursViewHeaderFormat) {
        this.hoursViewHeaderFormat = hoursViewHeaderFormat;
    }

    /**
     * Set format of header on days view.
     *
     * @param daysViewHeaderFormat format of header on days view.
     */
    public void setDaysViewHeaderFormat(final DateTimeFormat daysViewHeaderFormat) {
        this.daysViewHeaderFormat = daysViewHeaderFormat;
    }

    /**
     * Set format of header on months view.
     *
     * @param monthsViewHeaderFormat format of header on months view.
     */
    public void setMonthsViewHeaderFormat(final DateTimeFormat monthsViewHeaderFormat) {
        this.monthsViewHeaderFormat = monthsViewHeaderFormat;
    }

    /**
     * Set format of month names on months view.
     *
     * @param monthNamesFormat format of month names on month view.
     */
    @Override
    public void setMonthNamesFormat(final DateTimeFormat monthNamesFormat) {
        monthsGrid.setMonthNameFormat(monthNamesFormat);
    }

    /**
     * Set format of week day names on days of month grid.
     * By default is DateTimeFormat.getFormat("ccc").
     *
     * @param weekDayNamesFormat of week day names.
     */
    @Override
    public void setWeekDayNamesFormat(final DateTimeFormat weekDayNamesFormat) {
        daysView.setWeekDayNamesFormat(weekDayNamesFormat);
    }

    /**
     * Highlight value from data source.
     *
     * @param highlightValue if <code>true</code>, value from data source is
     *                       highlighted with isValue() style.
     *                       if <code>false</code>, value checking is disabled.
     */
    @Override
    public void setHighlightValue(final boolean highlightValue) {
        if (highlightValue && dataSource != null) {
            dataSourceValueChange = dataSource.addValueChangeHandler(new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(final ValueChangeEvent<Date> event) {

                    for (final DateTimePickerPanel panel : datePanels) {
                        panel.reloadChainedValue();
                    }
                }
            });
        } else {
            if (dataSourceValueChange != null)
                dataSourceValueChange.removeHandler();
        }
    }

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
    @Override
    public void setSecondClickToDrill(final boolean secondClickToDrill) {
        for (DateTimePickerPanel panel : datePanels) {
            panel.setSecondClickToDrill(secondClickToDrill);
        }
        this.secondClickToDrill = secondClickToDrill;
    }

    /**
     * Set appearance of decades grid. By default it is hidden.
     *
     * @param showDecadesGrid if <code>true</code>, century grid is visible.
     *                        if <code>false</code>, century grid is hidden.
     */
    @Override
    public void setShowDecadesGrid(final boolean showDecadesGrid) {
        if (showDecadesGrid) {
            decadesPanel.setVisible(true);
            yearsShiftUpHandlerRegistration = yearsPanel.addShiftUpHandler(
                    new ShiftUpEvent.Handler<Date>() {
                        @Override
                        public void onEvent(final ShiftUpEvent<Date> event) {
                            layerPanel.fireEvent(event);
                        }
                    }
            );

        } else {
            decadesPanel.setVisible(false);
            yearsShiftUpHandlerRegistration.removeHandler();
        }
    }
    //endregion

    //region Selected and shown date

    /**
     * @return value associated with current state.
     */
    @Override
    public Date getStateValue() {
        return shownDate;
    }

    /**
     * Sets value associated with current state.
     *
     * @param value associated with current state.
     */
    @Override
    public void setStateValue(final Date value) {
        this.shownDate = value;
    }

    /**
     * @return currently selected value.
     */
    @Override
    public Date getSelectedValue() {
        if (selectedDate == null) {
            selectedDate = getInitialDate();
        }
        return selectedDate;
    }

    /**
     * Sets currently selected value.
     *
     * @param value to be set as selected.
     */
    @Override
    public void setSelectedValue(final Date value) {
        this.selectedDate = value;
        ((DateTimePickerPanel) layerPanel.getWidget()).setSelectedValue(value);
    }
    //endregion

    //region ValueChain
    @Override
    public Date getChainedValue() {
        if (dataSource != null) {
            return CalendarUtil.copyDate(dataSource.getValue());
        } else {
            return null;
        }
    }

    @Override
    public void reloadChainedValue() {
        if (isAttached()) {
            for (DateTimePickerPanel panel : datePanels) {
                panel.reloadChainedValue();
            }
        }
    }
    //endregion

    //region Events
    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Date> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<Widget> handler) {
        return addHandler(handler, CloseEvent.getType());
    }
    //endregion

    @Override
    protected void onDetach() {
        super.onDetach();
        backNextCtrlArrowHandler.removeHandler();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        setSelectedValue(getSelectedValue());
        setHandleCtrlArrowToPage();
        layerPanel.setWidget(daysPanel); // initial view
    }

    private void bindCloseOnSelectMinute() {
        minutesPanel.addSelectionHandler(new SelectionHandler<Date>() {
            private Date previousSelected;

            @Override
            public void onSelection(final SelectionEvent<Date> event) {
                setSelectedValue(event.getSelectedItem());

                if (!secondClickToDrill) {
                    CloseEvent.fire(DateTimePicker.this, DateTimePicker.this);
                } else if (previousSelected != null
                        && previousSelected.equals(event.getSelectedItem())) {
                    CloseEvent.fire(DateTimePicker.this, DateTimePicker.this);
                }
                previousSelected = event.getSelectedItem();
            }
        });
    }

    private void bindSelectionAndCloseEvents() {
        for (final DateTimePickerPanel panel : datePanels) {
            panel.addSelectionHandler(new SelectionHandler<Date>() {
                @Override
                public void onSelection(final SelectionEvent<Date> event) {
                    setSelectedValue(event.getSelectedItem());
                    delegateEvent(DateTimePicker.this, event);

                }
            });
            panel.addCloseHandler(new CloseHandler<Widget>() {

                @Override
                public void onClose(final CloseEvent<Widget> event) {
                    setSelectedValue(((DateTimePickerPanel) event.getTarget()).getSelectedValue());
                    delegateEvent(DateTimePicker.this, event);
                }
            });
        }
    }

    private void delegateShiftDrill() {
        for (DateTimePickerPanel panel : datePanels) {

            panel.addDrillDownHandler(new DrillDownEvent.Handler<Date>() {
                @Override
                public void onEvent(final DrillDownEvent<Date> event) {
                    delegateEvent(layerPanel, event);
                }
            });

            panel.addShiftUpHandler(new ShiftUpEvent.Handler<Date>() {
                @Override
                public void onEvent(final ShiftUpEvent<Date> event) {
                    delegateEvent(layerPanel, event);
                }
            });

        }
    }

    /**
     * Synchronizes date shown on all layers.
     */
    @SuppressWarnings("unchecked")
    private void syncLayersShownDate() {

        for (DateTimePickerPanel panel : datePanels) {
            panel.addStateChangedHandler(new StateChangedEvent.Handler<Date>() {
                @Override
                public void onEvent(final StateChangedEvent<Date> event) {
                    setStateValue(event.getNewValue());
                }
            });
        }

        layerPanel.addDidPresentHandler(new DidAppearHandler<Date, IsWidget>() {
            @Override
            public void onDidPresent(DidAppearEvent<Date, IsWidget> event) {
                DateTimePickerPanel layerPanelWidget = (DateTimePickerPanel) layerPanel.getWidget();
                layerPanelWidget.setStateValue(getStateValue());
                layerPanelWidget.setSelectedValue(getSelectedValue());
            }
        });

        layerPanel.addWillPresentHandler(new WillAppearHandler<Date, IsWidget>() {
            @Override
            public void onWillPresent(WillAppearEvent<Date, IsWidget> event) {
                DateTimePickerPanel dissapearingWidget = (DateTimePickerPanel) layerPanel.getWidget();
                if (dissapearingWidget != null) {
                    setStateValue(dissapearingWidget.getStateValue());
                    // set directly to avoid calling grids select
                    selectedDate = dissapearingWidget.getSelectedValue();
                }
            }
        });
    }
    //endregion

    /**
     * @return if dataSource provides value - than from dataSource.
     * Otherwise - current date.
     */
    private Date getInitialDate() {
        if (initialDate != null) {
            return initialDate;
        } else {
            if (dataSource != null) {
                Date dsValue = dataSource.getValue();
                if (dsValue == null) {
                    Date date = new Date();
                    date.setMinutes(0);
                    date.setSeconds(0);
                    return date;
                } else {
                    return dataSource.getValue();
                }
            } else {
                Date date = new Date();
                date.setMinutes(0);
                date.setSeconds(0);
                return date;
            }
        }
    }

    private void setHandleCtrlArrowToPage() {
        backNextCtrlArrowHandler = Event.addNativePreviewHandler(new Event.NativePreviewHandler() {

            @Override
            public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (event.getTypeInt() == Event.ONKEYDOWN) {
                    NativeEvent ne = event.getNativeEvent();

                    if (ne.getCtrlKey() && ne.getKeyCode() == KeyCodes.KEY_RIGHT) {
                        event.cancel();
                        NextEvent.fire((DateTimePickerPanel) layerPanel.getWidget());
                    } else if (ne.getCtrlKey() && ne.getKeyCode() == KeyCodes.KEY_LEFT) {
                        event.cancel();
                        BackEvent.fire((DateTimePickerPanel) layerPanel.getWidget());
                    } else if (ne.getCtrlKey() && ne.getKeyCode() == KeyCodes.KEY_UP) {
                        event.cancel();
                        ShiftUpEvent.fire(layerPanel, getStateValue());
                    } else if (ne.getCtrlKey() && ne.getKeyCode() == KeyCodes.KEY_DOWN) {
                        event.cancel();
                        DrillDownEvent.fire(layerPanel, getStateValue());
                    }
                }
            }
        });
    }

    private String getDecadeString(Date date) {
        Date dateCopy = CalendarUtil.copyDate(date);

        StringBuilder result = new StringBuilder();
        dateCopy.setYear(CalendarUtil.getFirstYearInDecade(date));
        result.append(YEARS_DEFAULT_FORMAT.format(dateCopy));
        result.append(YEARS_HEADER_SEPARATOR);
        dateCopy.setYear(dateCopy.getYear() + CalendarUtil.YEARS_IN_DECADE);
        result.append(YEARS_DEFAULT_FORMAT.format(dateCopy));
        return result.toString();
    }

    private String getCenturyString(Date date) {
        Date dateCopy = CalendarUtil.copyDate(date);

        dateCopy.setYear(CalendarUtil.getFirstYearInCentury(dateCopy));
        String lastDecadeOfPrevCentStartYear = YEARS_DEFAULT_FORMAT.format(dateCopy);

        dateCopy.setYear(dateCopy.getYear() + CalendarUtil.YEARS_IN_CENTURY);
        String lastDecadeOfCentYear = YEARS_DEFAULT_FORMAT.format(dateCopy);

        return lastDecadeOfPrevCentStartYear +
                DECADES_HEADER_SEPARATOR +
                lastDecadeOfCentYear;
    }
}
