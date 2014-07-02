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

import com.github.gwt.user.client.ui.resources.CellGridResources;
import com.github.gwt.user.client.ui.resources.CellGridResourcesImpl;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.impl.ElementMapperImpl;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Grid with {@link Cell}s.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public abstract class CellGrid extends Grid {

    //region Cell
    @SuppressWarnings("deprecation")
    protected class Cell extends FocusWidget {
        private boolean enabled = true;
        private int index;

        Cell() {
            super(Document.get().createDivElement());

            index = cellList.size();
            cellList.add(this);

            elementToCell.put(this);
            addDomHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER ||
                            event.getNativeKeyCode() == ' ') {
                        if (isActive(Cell.this)) {
                            setSelected(Cell.this);
                        }
                    }
                }
            }, KeyDownEvent.getType());

            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setSelected(Cell.this);
                }
            }, ClickEvent.getType());
        }

        public int getIndex() {
            return index;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public boolean isHighlighted() {
            return this == highlightedCell;
        }

        public final void setEnabled(boolean enabled) {
            this.enabled = enabled;
            onEnabled(enabled);
        }

        protected void onEnabled(boolean enabled) {
            String disabledStyle = getDisabledStyle(index);
            if (enabled) {
                removeStyleName(disabledStyle);
            } else {
                addStyleName(disabledStyle);
            }
        }

        protected void onHighlighted(boolean highlighted) {
            String highlightedStyle = getHighlightedStyle(index);
            if (highlighted) {
                addStyleName(highlightedStyle);
            } else {
                removeStyleName(highlightedStyle);
            }
        }

        public void onSelected(boolean selected) {
            String selectedStyle = getSelectedStyle(index);
            if (selected) {
                addStyleName(selectedStyle);
            } else {
                removeStyleName(selectedStyle);
            }
        }

        private void setText(String value) {
            DOM.setInnerText(getElement(), value);
        }

        public void reload() {
            setText(getText(index));
            setStyleName(getCellStyle(index));
        }

        public void refreshHighligtedStyle() {
            onHighlighted(false);
            onHighlighted(isHighlighted());
        }
    }
    //endregion

    private static final CellGridResources DEFAULT_RESOURCES
            = CellGridResourcesImpl.cellGridResources();

    static {
        DEFAULT_RESOURCES.style().ensureInjected();
    }

    /**
     * Signals that key down selection is occurring.
     */
    protected boolean keyDownEventIsProcessing;
    private CellGridResources resources;
    private Cell highlightedCell;
    protected Cell selectedCell;
    private ElementMapperImpl<Cell> elementToCell = new ElementMapperImpl<Cell>();
    private List<Cell> cellList = new ArrayList<Cell>();

    //region Constructors
    protected CellGrid() {
        this(DEFAULT_RESOURCES);
    }

    protected CellGrid(CellGridResources resources) {
        this.resources = resources;
        if (this.resources != DEFAULT_RESOURCES) {
            getResources().style().ensureInjected();
        }

        // initially grid should be without padding, spacing, borders
        setCellPadding(0);
        setCellSpacing(0);
        setBorderWidth(0);

        sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);

        if (getResources() != null && getResources().style() != null) {
            setStyleName(getResources().style().grid());
        }
    }

    public void initGrid(int length) {
        int rows = (int) Math.floor(Math.sqrt(length));
        int columns = length / rows;
        if ((rows * columns) < length) {
            rows++;
        }
        initGrid(rows, columns);
    }

    public void initGrid(int rows, final int columns) {
        resize(rows, columns);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                final Cell cell = new Cell();
                setWidget(row, column, cell);
                addArrowKeysHandlers(columns, row, column, cell);
            }
        }
        reload();
    }

    protected void addArrowKeysHandlers(final int columns,
                                        final int row,
                                        final int column,
                                        final Cell cell) {

        cell.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                int keyCode = event.getNativeEvent().getKeyCode();
                if (keyCode == 0) {
                    keyCode = event.getNativeKeyCode();
                }
                switch (keyCode) {
                    case KeyCodes.KEY_DOWN: {
                        keyDownEventIsProcessing = true;
                        moveTo(getBottomCell(), event);
                        break;
                    }
                    case KeyCodes.KEY_UP: {
                        keyDownEventIsProcessing = true;
                        moveTo(getTopCell(), event);
                        break;
                    }
                    case KeyCodes.KEY_LEFT: {
                        keyDownEventIsProcessing = true;
                        moveTo(getLeftCell(), event);
                        break;
                    }
                    case KeyCodes.KEY_RIGHT: {
                        keyDownEventIsProcessing = true;
                        moveTo(getRightCell(), event);
                        break;
                    }
                }
                keyDownEventIsProcessing = false;
            }

            private void moveTo(Cell otherCell, KeyDownEvent event) {
                if (otherCell != null) {
                    cell.setFocus(false);
                    otherCell.setFocus(true);
                    setSelected(otherCell);
                    event.preventDefault();
                }
            }

            private Cell getBottomCell() {
                if ((row + 1) < getRowCount()) {
                    return getCell(((row + 1) * columns) + column);
                } else {
                    return getCell(column);
                }
            }

            public Cell getTopCell() {
                if (row > 0) {
                    return getCell(((row - 1) * columns) + column);
                } else {
                    return getCell(((getRowCount() - 1) * columns) + column);
                }
            }

            public Cell getLeftCell() {
                if (column == 0 && row == 0) {
                   return getCell(getNumCells() - 1);
                } else if (column > 0) {
                    return getCell((row * columns) + column - 1);
                } else {
                    return getCell(((row - 1) * columns) + (columns - 1));
                }
            }

            public Cell getRightCell() {
                if (row == (getRowCount() - 1) && column == (columns - 1)) {
                    return getCell(0);
                } else if (column < (columns - 1)) {
                    return getCell((row * columns) + column + 1);
                } else {
                    return getCell(((row + 1) * columns));
                }
            }
        });
    }

    //endregion

    //region Resources

    public CellGridResources getResources() {
        return resources;
    }

    public void setResources(CellGridResources resources) {
        this.resources = resources;
    }
    //endregion

    //region Cell configure
    protected abstract String getText(int index);

    protected String getCellStyle(int index) {
        return getResources().style().cell();
    }

    protected String getHighlightedStyle(int index) {
        return getResources().style().isHighlighted();
    }

    protected String getSelectedStyle(int index) {
        return getResources().style().isSelected();
    }

    protected String getDisabledStyle(int index) {
        return getResources().style().isDisabled();
    }
    //endregion Cell configure }}

    //region Cell getters
    private Cell getCell(Event e) {
        // Find out which cell was actually clicked.
        Element td = getEventTargetCell(e);
        return td != null
                ? elementToCell.get( td) : null;
    }

    protected Cell getCell(int i) {
        if (i < cellList.size()) {
            return cellList.get(i);
        } else {
            return null;
        }
    }

    protected int getNumCells() {
        return cellList.size();
    }

    public void setSelectedCell(final Cell selectedCell) {
        this.selectedCell = selectedCell;
        if (selectedCell != null) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                public void execute() {
                    selectedCell.setFocus(true);
                }
            });

            selectedCell.addStyleName(getSelectedStyle(selectedCell.getIndex()));
        }
    }

    protected Cell getSelectedCell() {
        return selectedCell;
    }
    //endregion

    @Override
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONCLICK: {
                Cell cell = getCell(event);
                if (isActive(cell)) {
                    setSelected(cell);
                }
                break;
            }
            case Event.ONMOUSEOUT: {
                Element e = DOM.eventGetFromElement(event);
                if (e != null) {
                    Cell cell = elementToCell.get(e);
                    if (cell == highlightedCell) {
                        setHighlighted(null);
                    }
                }
                break;
            }
            case Event.ONMOUSEOVER: {
                Element e = DOM.eventGetToElement(event);
                if (e != null) {
                    Cell cell = elementToCell.get(e);
                    if (isActive(cell)) {
                        setHighlighted(cell);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        setHighlighted(null);
    }

    public void reload() {
        for (int i = 0; i < getNumCells(); i++) {
            Cell cell = getCell(i);
            cell.reload();
        }
    }

    public final void setHighlighted(Cell nextHighlighted) {
        if (nextHighlighted == highlightedCell) return;

        Cell oldHighlighted = highlightedCell;
        highlightedCell = nextHighlighted;
        if (oldHighlighted != null) {
            oldHighlighted.onHighlighted(false);
        }
        if (highlightedCell != null) {
            highlightedCell.onHighlighted(true);
        }
    }

    protected void setSelected(Cell cell) {
        Cell last = getSelectedCell();
        setSelectedCell(cell);

        if (last != null) {
            last.onSelected(false);
        }

        onSelected((last != null ? last.getIndex() : 0), (selectedCell != null ? selectedCell.getIndex() : 0));

        if (selectedCell != null) {
            selectedCell.onSelected(true);
            selectedCell.refreshHighligtedStyle();
            selectedCell.setFocus(true);
        }
    }

    protected boolean isSelected(Cell cell) {
        return cell.equals(selectedCell);
    }

    protected abstract void onSelected(int lastSelectedIndex, int selectedIndex);

    private boolean isActive(Cell cell) {
        return cell != null && cell.isEnabled();
    }

}
