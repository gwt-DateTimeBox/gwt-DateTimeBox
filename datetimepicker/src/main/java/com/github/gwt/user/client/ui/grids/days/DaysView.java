package com.github.gwt.user.client.ui.grids.days;

import com.github.gwt.user.client.ui.ChainIsBrokenException;
import com.github.gwt.user.client.ui.ValueChain;
import com.github.gwt.user.client.ui.events.BackEvent;
import com.github.gwt.user.client.ui.events.NextEvent;
import com.github.gwt.user.client.ui.events.StateChangedEvent;
import com.github.gwt.user.client.ui.grids.DateGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

/**
 * {@link DaysGrid} with {@link DaysOfWeekView} header.
 * Delegates all handlers to {@link DaysGrid}.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public class DaysView extends Composite implements DateGrid {
    interface DaysViewUiBinder extends UiBinder<VerticalPanel, DaysView> {
    }

    private static DaysViewUiBinder ourUiBinder
            = GWT.create(DaysViewUiBinder.class);

    @UiField(provided = true)
    DaysGrid grid;
    @UiField
    DaysOfWeekView weekDayNames;

    public DaysView(Date initialDate) {
        this.grid = new DaysGrid(initialDate);
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /**
     * Set appearance of header with week days names on days of month view.
     * By default is visible.
     *
     * @param showWeekDayNames if <code>true</code>, header with names
     *                         of week days is visible.
     *                         if <code>false</code>, header with names
     *                         of week days is invisible.
     */
    public void setShowWeekDayNames(final boolean showWeekDayNames) {
        weekDayNames.setVisible(showWeekDayNames);
    }

    /**
     * Set format of week day names on days of month grid.
     * By default is DateTimeFormat.getFormat("ccc").
     *
     * @param weekDayNamesFormat of week day names.
     */
    public void setWeekDayNamesFormat(final DateTimeFormat weekDayNamesFormat) {
        weekDayNames.setDayOfWeekFormat(weekDayNamesFormat);
    }

    /**
     * Adds a {@link com.google.gwt.event.logical.shared.SelectionEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    @Override
    public HandlerRegistration addSelectionHandler(
            final SelectionHandler<Date> handler) {
        return grid.addSelectionHandler(handler);
    }

    /**
     * Adds a {@link StateChangedEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event.
     */
    @Override
    public HandlerRegistration addStateChangedHandler(
            final StateChangedEvent.Handler<Date> handler) {
        return grid.addStateChangedHandler(handler);
    }

    /**
     * @return value associated with current state.
     */
    @Override
    public Date getStateValue() {
        return grid.getStateValue();
    }

    /**
     * Sets value associated with current state.
     *
     * @param value associated with current state.
     */
    @Override
    public void setStateValue(final Date value) {
        grid.setStateValue(value);
    }

    /**
     * @return currently selected value.
     */
    @Override
    public Date getSelectedValue() {
        return grid.getSelectedValue();
    }

    /**
     * Sets currently selected value.
     *
     * @param value to be set as selected.
     */
    @Override
    public void setSelectedValue(final Date value) {
        grid.setSelectedValue(value);
    }

    @Override
    public void fireEvent(final GwtEvent<?> event) {
        grid.fireEvent(event);
    }

    /**
     * Adds a {@link BackEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event.
     */
    @Override
    public HandlerRegistration addBackHandler(final BackEvent.Handler handler) {
        return grid.addBackHandler(handler);
    }

    /**
     * Adds a {@link NextEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event.
     */
    @Override
    public HandlerRegistration addNextHandler(final NextEvent.Handler handler) {
        return grid.addNextHandler(handler);
    }

    //region Common Chain behavior
    @Override
    public Date getChainedValue() {
        if (isAttached()) {
            Widget parent = getParent();
            while (parent != null) {
                if (parent instanceof ValueChain) {
                    ValueChain parentChain = (ValueChain) parent;
                    return (Date) parentChain.getChainedValue();
                }
                parent = parent.getParent();
            }
        }
        throw new ChainIsBrokenException();
    }

    @Override
    public void reloadChainedValue() {
        if (isAttached()) {
            grid.reloadChainedValue();
        }
    }

}