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

import com.github.gwt.user.client.ui.resources.DateTimeBoxResources;
import com.github.gwt.user.client.ui.resources.DateTimeBoxResourcesImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;

import java.util.Date;

/**
 * Input box for date value (with date time picker).
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public class DateTimeBox extends Composite implements HasValue<Date> {

    interface DateTimeBoxUiBinder
            extends UiBinder<HorizontalPanel, DateTimeBox> {}
    private static DateTimeBoxUiBinder ourUiBinder
            = GWT.create(DateTimeBoxUiBinder.class);

    private static final DateTimeBoxResources DEFAULT_RESOURCES
            = DateTimeBoxResourcesImpl.dateTimeBoxResources();

    static {
        DEFAULT_RESOURCES.style().ensureInjected();
    }

    //region UiFields
    @UiField
    ClearDateBox box;
    @UiField(provided = true)
    FocusWidget button;
    //endregion

    private PopupPanel popup;
    private DateTimeBoxResources resources;
    private DateTimePicker dateTimePicker;

    @UiConstructor
    public DateTimeBox() {
        this(null);
    }

    public DateTimeBox(FocusWidget calendarButton) {
        this(calendarButton, DEFAULT_RESOURCES);
    }

    public DateTimeBox(FocusWidget calendarButton,
                       DateTimeBoxResources resources) {

        if (resources == null) {
            this.resources = DEFAULT_RESOURCES;
        } else if (resources != DEFAULT_RESOURCES) {
            resources.style().ensureInjected();
            this.resources = resources;
        } else {
            this.resources = DEFAULT_RESOURCES;
        }

        if (calendarButton == null) {
            button = defaultButton();
        } else {
            button = calendarButton;
        }

        initWidget(ourUiBinder.createAndBindUi(this));

        assert resources != null;
        getWidget().setStyleName(resources.style().dateTimeBox());
        button.setStyleName(resources.style().calButton());

        initPopup(box);
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        popup.showRelativeTo(button);
    }

    public ClearBoxSpecification<String> clearBox() {
        return box;
    }

    public DateTimePickerSpecification dateTimePicker() {
        return dateTimePicker;
    }

    //region HasValue<Date>
    @Override
    public Date getValue() {
        return box.getDate();
    }

    @Override
    public void setValue(Date value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Date value, boolean fireEvents) {
        Date oldValue = box.getDate();
        box.setDate(value);
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
    //endregion HasValue<Date>

    private FocusWidget defaultButton() {
        PushButton button = new PushButton();
        Image up = new Image(resources.calButton().up());
        button.getUpFace().setImage(up);
        Image upHover = new Image(resources.calButton().upHover());
        button.getUpHoveringFace().setImage(upHover);
        Image upDisable = new Image(resources.calButton().upDisabled());
        button.getUpDisabledFace().setImage(upDisable);
        Image down = new Image(resources.calButton().down());
        button.getDownFace().setImage(down);
        Image downHover = new Image(resources.calButton().downHover());
        button.getDownHoveringFace().setImage(downHover);
        Image downDsbl = new Image(resources.calButton().downDsbld());
        button.getDownDisabledFace().setImage(downDsbl);
        return button;
    }

    private void initPopup(UIObject autoHidePartner) {
        this.popup = new PopupPanel(true);
        popup.addAutoHidePartner(autoHidePartner.getElement());
        popup.setStyleName(resources.style().popup());

        dateTimePicker = initPicker();
        popup.setWidget(dateTimePicker);

        @SuppressWarnings("deprecation")
        Element popupContent = DOM.getParent(dateTimePicker.getElement());
        popupContent.setAttribute("style", "height: 100%;");

        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                delegateEvent(dateTimePicker, event);
            }
        });
    }

    @SuppressWarnings("deprecation")
    protected DateTimePicker initPicker() {
        Date initial = new Date();
        initial.setMinutes(0);
        initial.setSeconds(0);
        final DateTimePicker dateTimePicker = new DateTimePicker(this, initial);
        dateTimePicker.addSelectionHandler(new SelectionHandler<Date>() {
            @Override
            public void onSelection(SelectionEvent<Date> event) {
                setValue(event.getSelectedItem(), true);
            }
        });
        dateTimePicker.addCloseHandler(new CloseHandler<Widget>() {
            @Override
            public void onClose(CloseEvent<Widget> event) {
                setValue(dateTimePicker.getSelectedValue(), true);
                popup.hide();
            }
        });
        return dateTimePicker;
    }

}