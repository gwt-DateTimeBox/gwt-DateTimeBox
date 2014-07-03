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
import com.github.gwt.user.client.ui.grids.DateGrid;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

/**
 * Panel with some {@link com.github.gwt.user.client.ui.grids.DateGrid} and
 * {@link com.github.gwt.user.client.ui.Pager} bound together.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
class DateTimePickerPanel extends VerticalPanel
        implements HasStateValue<Date>,
        HasSelectedValue<Date>,
        HasShiftUpHandlers<Date>,
        HasDrillDownHandlers<Date>,
        HasSelectionHandlers<Date>,
        HasStateChangedHandlers<Date>,
        HasNextHandlers,
        HasBackHandlers,
        HasCloseHandlers<Widget>,
        ValueChain<Date> {

    private Pager pager;
    private DateGrid grid;

    private FormatDateStrategy formatStrategy;
    private boolean secondClickToDrill;

    public DateTimePickerPanel(Pager pager, DateGrid grid) {
        this.pager = pager;
        this.grid = grid;
        bindPagerAndGrid();

        add(pager);
        add(grid);
    }

    public void setFormatStrategy(final FormatDateStrategy formatStrategy) {
        this.formatStrategy = formatStrategy;

        if (formatStrategy != null) {
            String headerString = formatStrategy.format(grid.getStateValue());
            pager.setText(headerString);
        }
    }

    private FormatDateStrategy getFormatStrategy() {
        return formatStrategy;
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
     * Binds next, back events. Syncs pager text with state value of grid.
     */
    private void bindPagerAndGrid() {

        pager.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(final AttachEvent event) {
                if (getFormatStrategy() != null) {
                    String headerString = getFormatStrategy().format(grid.getStateValue());
                    pager.setText(headerString);
                }
            }
        });

        grid.addStateChangedHandler(new StateChangedEvent.Handler<Date>() {
            @Override
            public void onEvent(final StateChangedEvent<Date> event) {
                if (getFormatStrategy() != null) {
                    String headerString = getFormatStrategy().format(grid.getStateValue());
                    pager.setText(headerString);
                }
            }
        });

        pager.addNextHandler(new NextEvent.Handler() {
            @Override
            public void onEvent(final NextEvent event) {
                delegateEvent(grid.asWidget(), event);
            }
        });

        addNextHandler(new NextEvent.Handler() {
            @Override
            public void onEvent(final NextEvent event) {
                delegateEvent(pager, event);
            }
        });

        pager.addBackHandler(new BackEvent.Handler() {
            @Override
            public void onEvent(final BackEvent event) {
                delegateEvent(grid.asWidget(), event);
            }
        });

        addBackHandler(new BackEvent.Handler() {
            @Override
            public void onEvent(final BackEvent event) {
                delegateEvent(pager, event);
            }
        });

        pager.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ShiftUpEvent.fire(DateTimePickerPanel.this, grid.getStateValue());
            }
        });

        grid.addSelectionHandler(new SelectionHandler<Date>() {
            private Date previousSelected;

            @Override
            public void onSelection(SelectionEvent<Date> event) {
                Date selectedItem = event.getSelectedItem();

                if (secondClickToDrill) {
                    if (selectedItem != null && selectedItem.equals(previousSelected)) {
                        DrillDownEvent.fire(DateTimePickerPanel.this, grid.getStateValue());
                    }
                } else {
                    DrillDownEvent.fire(DateTimePickerPanel.this, grid.getStateValue());
                }
                previousSelected = selectedItem;
            }
        });
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
    public void setSecondClickToDrill(final boolean secondClickToDrill) {
        this.secondClickToDrill = secondClickToDrill;
    }

    /**
     * Adds a {@link DrillDownEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event.
     */
    @Override
    public HandlerRegistration addDrillDownHandler(final DrillDownEvent.Handler<Date> handler) {
        return addHandler(handler, DrillDownEvent.TYPE);
    }

    /**
     * Adds a {@link ShiftUpEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event.
     */
    @Override
    public HandlerRegistration addShiftUpHandler(final ShiftUpEvent.Handler<Date> handler) {
        return addHandler(handler, ShiftUpEvent.TYPE);
    }

    /**
     * Adds a {@link com.google.gwt.event.logical.shared.SelectionEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    @Override
    public HandlerRegistration addSelectionHandler(final SelectionHandler<Date> handler) {
        return grid.addSelectionHandler(handler);
    }

    /**
     * Adds a {@link StateChangedEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event.
     */
    @Override
    public HandlerRegistration addStateChangedHandler(final StateChangedEvent.Handler<Date> handler) {
        return grid.addStateChangedHandler(handler);
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

    /**
     * Adds a {@link BackEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event.
     */
    @Override
    public HandlerRegistration addBackHandler(final BackEvent.Handler handler) {
        return addHandler(handler, BackEvent.TYPE);
    }

    /**
     * Adds a {@link NextEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event.
     */
    @Override
    public HandlerRegistration addNextHandler(final NextEvent.Handler handler) {
        return addHandler(handler, NextEvent.TYPE);
    }

    /**
     * Adds a {@link com.google.gwt.event.logical.shared.CloseEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    @Override
    public HandlerRegistration addCloseHandler(final CloseHandler<Widget> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

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
            Widget widget = asWidget();
            if (widget instanceof ComplexPanel) {
                ComplexPanel childPanel = (ComplexPanel) widget;
                for (Widget next : childPanel) {
                    if (next instanceof ValueChain) {
                        ValueChain nextChain = (ValueChain) next;
                        nextChain.reloadChainedValue();
                    }
                }
            }
        }
    }
}
