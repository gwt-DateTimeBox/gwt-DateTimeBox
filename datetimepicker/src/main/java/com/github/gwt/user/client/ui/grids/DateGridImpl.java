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

package com.github.gwt.user.client.ui.grids;

import com.github.gwt.user.client.ui.CellGrid;
import com.github.gwt.user.client.ui.ChainIsBrokenException;
import com.github.gwt.user.client.ui.ValueChain;
import com.github.gwt.user.client.ui.events.BackEvent;
import com.github.gwt.user.client.ui.events.NextEvent;
import com.github.gwt.user.client.ui.events.StateChangedEvent;
import com.github.gwt.user.client.ui.grids.resources.DateGridResources;
import com.github.gwt.user.client.ui.grids.resources.DateGridResourcesImpl;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

/**
 * Abstract DateGrid with handling filler, next and back,
 * selection cases.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public abstract class DateGridImpl extends CellGrid implements DateGrid {

    private static final DateGridResources DEFAULT_RESOURCES
            = DateGridResourcesImpl.dateGridResources();

    static {
        DEFAULT_RESOURCES.style().ensureInjected();
    }

    protected Date stateValue;
    private final Date defaultStateValue;
    private DateGridResources resources;
    private Cell valueCell;

    protected DateGridImpl(Date defaultStateValue, DateGridResources resources) {
        this.defaultStateValue = defaultStateValue;

        if (resources == null) resources = DEFAULT_RESOURCES;
        this.resources = resources;
        if (this.resources != DEFAULT_RESOURCES) {
            getResources().style().ensureInjected();
        }
        setStyleName(resources.style().grid());

        addBackNextHandlers();
    }

    protected abstract Date prevStateValue();

    protected abstract Date nextStateValue();

    protected abstract Date getDate(int index);

    protected abstract Cell getCell(Date date);

    public void setResources(final DateGridResources resources) {
        this.resources = resources;
    }

    @Override
    public DateGridResources getResources() {
        return resources;
    }

    private void addBackNextHandlers() {
        addBackHandler(new BackEvent.Handler() {
            @Override
            public void onEvent(BackEvent event) {
                Date prevStateVal = prevStateValue();
                Date selectedValue = getSelectedValue();
                CellGrid.Cell last = getCell(selectedValue);
                if (last != null) {
                    last.onSelected(false);
                    setSelectedCell(null);
                }
                setStateValue(prevStateVal);
                Cell selectedCell = getCell(selectedValue);
                if (selectedCell != null) {
                    selectedCell.onSelected(true);
                    setSelectedCell(selectedCell);
                }
            }
        });
        addNextHandler(new NextEvent.Handler() {
            @Override
            public void onEvent(NextEvent event) {
                Date nextStateValue = nextStateValue();
                Date selectedValue = getSelectedValue();
                Cell last = getCell(selectedValue);
                if (last != null) {
                    last.onSelected(false);
                    setSelectedCell(null);
                }
                setStateValue(nextStateValue);
                Cell selectedCell = getCell(selectedValue);
                if (selectedCell != null) {
                    selectedCell.onSelected(true);
                    setSelectedCell(selectedCell);
                }

            }
        });
    }

    @Override
    protected void setSelected(Cell cell) {
        int index = cell.getIndex();
        if (isFiller(index)) {
            if (index < (getNumCells() / 2)) {
                int topFillerOffset = getTopFillerOffset(cell);
                setStateValue(getDate(cell.getIndex()));
                selectLastCellFromFillerOffset(topFillerOffset);
            } else {
                int bottomFillerOffset = getBottomFillerOffset(cell);
                setStateValue(getDate(cell.getIndex()));
                selectFirstCellFromFillerOffset(bottomFillerOffset);
            }
        } else {
            super.setSelected(cell);
        }
    }

    protected void selectFirstCellFromFillerOffset(int fillerOffset) {
        int firstIndex = 0;
        while (isFiller(firstIndex) || fillerOffset > 0) {
            if (!isFiller(firstIndex)) {
                fillerOffset--;
            }
            firstIndex++;
        }
        setSelected(getCell(firstIndex));
    }

    protected int getBottomFillerOffset(Cell cell) {
        int selectedCellIndex = cell.getIndex();
        int fillerOffset = 0;
        int cellIndex = (getNumCells() / 2);
        while (cellIndex < selectedCellIndex) {
            if (isFiller(cellIndex)) {
                fillerOffset++;
            }
            cellIndex++;
        }
        return fillerOffset;
    }

    protected void selectLastCellFromFillerOffset(int fillerOffset) {
        int lastIndex = getNumCells() - 1;
        while (isFiller(lastIndex) || fillerOffset > 0) {
            if (!isFiller(lastIndex)) {
                fillerOffset--;
            }
            lastIndex--;
        }
        setSelected(getCell(lastIndex));
    }

    protected int getTopFillerOffset(Cell selectedFillerCell) {
        int selectedCellIndex = selectedFillerCell.getIndex();
        int fillerOffset = 0;
        int cellIndex = (getNumCells() / 2);
        while (cellIndex > selectedCellIndex) {
            if (isFiller(cellIndex)) {
                fillerOffset++;
            }
            cellIndex--;
        }
        return fillerOffset;
    }

    protected String getIsFillerStyle(int index) {
        return getResources().style().isFiller();
    }


    protected String getIsValueAndHighlightedStyle(int index) {
        return getResources().style().isValueAndHighlighted();
    }

    protected String getIsValueStyle(int index) {
        return getResources().style().isValue();
    }

    @Override
    public void reload() {
        super.reload();
        int numCells = getNumCells();
        for (int i = 0; i < numCells; i++) {
            Cell cell = getCell(i);
            if (isFiller(i)) {
                cell.addStyleName(getIsFillerStyle(i));
            }
            if (isSelected(cell)) {
                cell.addStyleName(getSelectedStyle(i));
                cell.setFocus(true);
            }
        }
    }

    protected boolean isFiller(int index) {
        return false;
    }

    @Override
    public HandlerRegistration addBackHandler(BackEvent.Handler handler) {
        return addHandler(handler, BackEvent.TYPE);
    }

    @Override
    public HandlerRegistration addNextHandler(NextEvent.Handler handler) {
        return addHandler(handler, NextEvent.TYPE);
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
            if (valueCell != null) {
                valueCell.removeStyleName(
                        getIsValueAndHighlightedStyle(valueCell.getIndex()));
                valueCell.removeStyleName(
                        getIsValueStyle(valueCell.getIndex()));
            }

            valueCell = getCell(getChainedValue());
            if (valueCell != null) {
                int valueIndex = valueCell.getIndex();
                if (valueCell.isHighlighted()) {
                    valueCell.addStyleName(
                            getIsValueAndHighlightedStyle(valueIndex));
                } else {
                    valueCell.addStyleName(getIsValueStyle(valueIndex));
                }
            }
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        reloadChainedValue();
    }

    //endregion

    //region State and Selection

    public Date getStateValue() {
        if (stateValue == null) {
            stateValue = defaultStateValue;
        }
        return stateValue;
    }

    @Override
    public void setStateValue(Date value) {
        if (stateValue != null && value != null) {
            boolean needsReload = needsReload(stateValue, value);
            stateValue = value;
            if (needsReload) {
                reload();
            }
        } else {
            stateValue = value;
        }

        StateChangedEvent.fire(this, stateValue);
    }

    protected boolean needsReload(Date oldStateValue, Date newStateValue) {
        return false;
    }

    @Override
    protected void onSelected(int lastSelectedIndex, int selectedIndex) {
        Date newSelectedValue = getDate(selectedIndex);
        setStateValue(newSelectedValue);
        selectedValue = newSelectedValue;
        if (!keyDownEventIsProcessing) {
            SelectionEvent.fire(this, selectedValue);
        }
    }

    private Date selectedValue;

    public void setSelectedValue(Date value) {
        selectedValue = value;
        Cell selectedCell = getCell(value);
        Cell last = getSelectedCell();
        if (selectedCell != null) {
            selectedCell.onSelected(true);
        }
        if (last != null) {
            last.onSelected(false);
        }
        setSelectedCell(selectedCell);
    }

    public Date getSelectedValue() {
        return selectedValue;
    }

    @Override
    public HandlerRegistration addStateChangedHandler(StateChangedEvent.Handler<Date> handler) {
        return addHandler(handler, StateChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Date> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }
    //endregion

    protected boolean notOnGrid(final Date date) {
        Date topLeftCornerDate = getDate(0);
        Date bottomRightDate = getDate(getNumCells() - 1);
        // in order to avoid Gregorian versus Julian
        // calendar class cast exceptions
        return date.getTime() < topLeftCornerDate.getTime()
                || date.getTime() > bottomRightDate.getTime();
    }
}
