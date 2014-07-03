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

import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.TextBox;

import java.util.Date;

/**
 * Input box with clear button, containing date value.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
class ClearDateBox extends ClearBox<String> implements HasDate,
                                                       HasFocusHandlers {

    @SuppressWarnings("deprecation")
    private static final DateTimeFormat DEFAULT_FORMAT
            = DateTimeFormat.getShortDateTimeFormat();
    private DateTimeFormat format;
    boolean valueIsValid = true;

    @UiConstructor
    public ClearDateBox() {
        this(DEFAULT_FORMAT);
    }

    public ClearDateBox(DateTimeFormat format) {
        super(new TextBox());
        this.format = format;
        setPreprocessAlgorithm(new PreprocessAlgorithm<String>() {
            @Override
            public String preprocess(final String value) {
                Date date = tryParseWithAllPreDefFormats(value);
                if (date != null) {
                    valueIsValid = true;
                    return getFormat().format(date);
                } else {
                    valueIsValid = false;
                    return value;
                }
            }
        });

        setValidationAlgorithm(new ValidationAlgorithm<String>() {
            @Override
            public boolean validate(final String value) {
                return valueIsValid;
            }
        });
        setClearButtonVisible(false);
    }

    public Date getDate() {
        return tryParseWithAllPreDefFormats(getValue());
    }

    public void setDate(Date date) {
        if (date != null) {
            setValue(getFormat().format(date));
        }
    }

    public DateTimeFormat getFormat() {
        return format;
    }

    public void setFormat(DateTimeFormat format) {
        this.format = format;
    }

    /**
     * Adds a {@link com.google.gwt.event.dom.client.FocusEvent} handler.
     *
     * @param handler the focus handler
     * @return {@link com.google.gwt.event.shared.HandlerRegistration} used to remove this handler
     */
    @Override
    public HandlerRegistration addFocusHandler(final FocusHandler handler) {
        return valueBox.addFocusHandler(handler);
    }

    private Date tryParseWithAllPreDefFormats(String value) {
        if (value!= null && !value.equalsIgnoreCase(getPlaceholder())) {
            for (DateTimeFormat.PredefinedFormat format : DateTimeFormat.PredefinedFormat.values()) {
                try {
                    return DateTimeFormat.getFormat(format).parse(value);
                } catch (IllegalArgumentException e) {
                    // try all formats. if all fails - null
                }
            }
        }
        return null;
    }
}
