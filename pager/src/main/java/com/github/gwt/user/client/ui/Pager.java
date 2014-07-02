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

import com.github.gwt.user.client.ui.events.BackEvent;
import com.github.gwt.user.client.ui.events.HasBackHandlers;
import com.github.gwt.user.client.ui.events.HasNextHandlers;
import com.github.gwt.user.client.ui.events.NextEvent;
import com.github.gwt.user.client.ui.resources.PagerResources;
import com.github.gwt.user.client.ui.resources.PagerResourcesImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Simple {@link com.google.gwt.user.client.ui.TextBox} with back and
 * forward buttons. Fires {@link BackEvent} and {@link NextEvent} on button
 * presses.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public class Pager extends Composite implements HasNextHandlers,
                                                HasBackHandlers,
                                                HasClickHandlers,
                                                HasText {

    interface PagerUiBinder extends UiBinder<HorizontalPanel, Pager> {
    }
    private static PagerUiBinder ourUiBinder = GWT.create(PagerUiBinder.class);
    private static final PagerResources RESOURCES
            = PagerResourcesImpl.pagerResources();

    static {
        RESOURCES.style().ensureInjected();
    }

    @UiField
    PushButton forwards;
    @UiField
    PushButton backwards;
    @UiField
    TextBox center;

    public Pager() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("backwards")
    void onBackwardsClick(ClickEvent event) {
        BackEvent.fire(this);
    }

    @UiHandler("forwards")
    void onForwardsClick(ClickEvent event) {
        NextEvent.fire(this);
    }

    @Override
    /**
     * Gets this object's text.
     *
     * @return the object's text
     */
    public String getText() {
        return center.getText();
    }

    /**
     * Sets this object's text.
     *
     * @param text the object's new text
     */
    @Override
    public void setText(final String text) {
        center.setText(text);
    }

    @Override
    public HandlerRegistration addBackHandler(BackEvent.Handler handler) {
        return addHandler(handler, BackEvent.TYPE);
    }

    @Override
    public HandlerRegistration addNextHandler(NextEvent.Handler handler) {
        return addHandler(handler, NextEvent.TYPE);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return center.addClickHandler(handler);
    }
}