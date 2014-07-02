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

import com.github.gwt.user.client.ui.events.DrillDownEvent;
import com.github.gwt.user.client.ui.events.HasDrillDownHandlers;
import com.github.gwt.user.client.ui.events.HasShiftUpHandlers;
import com.github.gwt.user.client.ui.events.ShiftUpEvent;
import com.github.gwt.user.client.ui.events.appear.did.DidAppearEvent;
import com.github.gwt.user.client.ui.events.appear.did.DidAppearHandler;
import com.github.gwt.user.client.ui.events.appear.did.HasDidAppearHandlers;
import com.github.gwt.user.client.ui.events.appear.will.HasWillAppearHandlers;
import com.github.gwt.user.client.ui.events.appear.will.WillAppearEvent;
import com.github.gwt.user.client.ui.events.appear.will.WillAppearHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A LayerPanel navigator container is made up of a collection of child
 * containers that are layered on top of each other, with only one container
 * visible, or active, at a time.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public class LayerPanel<T> extends SimplePanel
                           implements IsWidget,
                                      HasDrillDownHandlers<T>,
                                      HasShiftUpHandlers<T>,
                                      HasWillAppearHandlers<T, IsWidget>,
                                      HasDidAppearHandlers<T, IsWidget>,
                                      HasWidgets {

    protected int cursor;
    protected List<IsWidget> widgetList = new ArrayList<IsWidget>();

    @UiConstructor
    public LayerPanel() {
        bindLayers();
    }

    @Override
    public void add(Widget w) {
        widgetList.add(w);
    }

    @Override
    public void clear() {
        super.clear();
        widgetList.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(widgetList.get(cursor).asWidget());
        return widgets.iterator();
    }

    @Override
    public void setWidget(Widget w) {
        WillAppearEvent.fire(this, w);

        if (widgetList.contains(w)) {
            cursor = widgetList.indexOf(w);
        } else {
            widgetList.set(cursor, w);
        }
        super.setWidget(w);

        DidAppearEvent.fire(this, w);
    }

    @Override
    public HandlerRegistration addDrillDownHandler(DrillDownEvent.Handler<T> handler) {
        return addHandler(handler, DrillDownEvent.TYPE);
    }

    @Override
    public HandlerRegistration addShiftUpHandler(ShiftUpEvent.Handler<T> handler) {
        return addHandler(handler, ShiftUpEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDidPresentHandler(DidAppearHandler<T, IsWidget> handler) {
        return addHandler(handler, DidAppearEvent.TYPE);
    }

    @Override
    public HandlerRegistration addWillPresentHandler(WillAppearHandler<T, IsWidget> handler) {
        return addHandler(handler, WillAppearEvent.TYPE);
    }

    /**
     * Present next widget in hierarchy on {@link ShiftUpEvent}, and previous
     * on {@link DrillDownEvent}
     */
    private void bindLayers() {

        addDrillDownHandler(new DrillDownEvent.Handler<T>() {
            @Override
            public void onEvent(DrillDownEvent<T> event) {
                if (cursor > 0) {
                    IsWidget bottom = widgetList.get(--cursor);
                    if (bottom != null) {
                        setWidget(bottom.asWidget());
                    }
                }
            }
        });
        addShiftUpHandler(new ShiftUpEvent.Handler<T>() {
            @Override
            public void onEvent(ShiftUpEvent<T> event) {
                if (cursor < (widgetList.size() - 1)) {
                    IsWidget top = widgetList.get(++cursor);
                    if (top != null) {
                        setWidget(top.asWidget());
                    }
                }
            }
        });
    }
}