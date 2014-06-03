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

import com.github.gwt.user.client.ui.events.ClearEvent;
import com.github.gwt.user.client.ui.resources.ClearBoxResources;
import com.github.gwt.user.client.ui.resources.ClearBoxResourcesImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Composite widget with some {@link com.google.gwt.user.client.ui.ValueBoxBase}
 * and some {@link com.google.gwt.user.client.ui.FocusWidget} clear button.
 * Implements {@link ClearBoxSpecification}, so has a placeholder, validation
 * and preprocess algorithms.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public class ClearBox<T> extends Composite implements ClearBoxSpecification<T> {

    interface ClearBoxUiBinder extends UiBinder<HorizontalPanel, ClearBox> {}
    private static ClearBoxUiBinder ourUiBinder
                                         = GWT.create(ClearBoxUiBinder.class);

    //region Default resources injection
    private static final ClearBoxResources DEFAULT_RESOURCES
            = ClearBoxResourcesImpl.clearBoxResources();

    static {
        DEFAULT_RESOURCES.style().ensureInjected();
    }
    //endregion Default resources injection

    //region ivars
    private final ClearBoxResources RESOURCES;
    private boolean clearButtonVisible = true;
    private boolean clearButtonVisibleWhileEmpty = false;
    private boolean isEnabled = true;
    private boolean readOnly;
    private boolean enteredValueEqualsPlaceholder;
    private T placeHolder;
    private ValidationAlgorithm<T> validationAlgorithm;
    private PreprocessAlgorithm<T> preprocessAlgorithm;

    //region Style
    private String emptyStyle     = DEFAULT_RESOURCES.style().empty();
    private String filledStyle    = DEFAULT_RESOURCES.style().filled();
    private String readOnlyStyle  = DEFAULT_RESOURCES.style().readOnly();
    private String incorrectValueBoxStyle
                        = DEFAULT_RESOURCES.style().incorrectValueBox();

    private String incorrectStyle = DEFAULT_RESOURCES.style().incorrect();
    private String correctStyle   = DEFAULT_RESOURCES.style().correct();
    private String disabledStyle  = DEFAULT_RESOURCES.style().disabled();
    //endregion

    //endregion ivars

    //region UiBinder fields
    @UiField(provided = true)
    final ValueBoxBase<T> valueBox;
    @UiField(provided = true)
    FocusWidget clearButton;
    //endregion UiBinder fields

    //region Construct widget
    public ClearBox(ValueBoxBase<T> valueBox) {
        this(valueBox, null);
    }

    public ClearBox(ValueBoxBase<T> valueBox, FocusWidget clearButton) {
        this(valueBox, clearButton, DEFAULT_RESOURCES);
    }

    public ClearBox(ValueBoxBase<T> valueBox,
                    FocusWidget clearButton,
                    ClearBoxResources resources) {

        this.valueBox = valueBox;
        this.RESOURCES = resources;
        if (RESOURCES != DEFAULT_RESOURCES) {
            RESOURCES.style().ensureInjected();
            this.emptyStyle = RESOURCES.style().empty();
            this.filledStyle = RESOURCES.style().filled();
            this.incorrectValueBoxStyle = RESOURCES.style().incorrectValueBox();
            this.incorrectStyle = RESOURCES.style().incorrect();
            this.correctStyle = RESOURCES.style().correct();
            this.disabledStyle= RESOURCES.style().disabled();
            this.readOnlyStyle = RESOURCES.style().readOnly();
        }
        if (clearButton == null) {
            this.clearButton = defaultClearButton();
        } else {
            this.clearButton = clearButton;
        }

        initWidget(ourUiBinder.createAndBindUi(this));

        setClearButtonVisibility(clearButtonVisibleWhileEmpty);
        this.valueBox.setValue(getPlaceholder());
        this.valueBox.setStyleName(emptyStyle);
    }

    private FocusWidget defaultClearButton() {
        PushButton pushClearButton = new PushButton();
        Image upImage = new Image(RESOURCES.clearButton().up());
        pushClearButton.getUpFace().setImage(upImage);
        Image hover = new Image(RESOURCES.clearButton().upHovering());
        pushClearButton.getUpHoveringFace().setImage(hover);
        Image disabled = new Image(RESOURCES.clearButton().upDisabled());
        pushClearButton.getUpDisabledFace().setImage(disabled);
        Image down = new Image(RESOURCES.clearButton().down());
        pushClearButton.getDownFace().setImage(down);
        Image downHover = new Image(RESOURCES.clearButton().downHover());
        pushClearButton.getDownHoveringFace().setImage(downHover);
        Image downDisabled = new Image(RESOURCES.clearButton().downDisabled());
        pushClearButton.getDownDisabledFace().setImage(downDisabled);
        return pushClearButton;
    }
    //endregion Construct UiBinder fields UiBinder fields

    //region UiBinder handlers
    @UiHandler("clearButton")
    void onClearButtonClick(ClickEvent event) {
        setValue(null);
    }

    @UiHandler("valueBox")
    void onValueBoxFocus(FocusEvent event) {
        if (!readOnly && isEmpty()) {
            valueBox.setCursorPos(0);
        }
    }

    @UiHandler("valueBox")
    void onValueBoxClick(ClickEvent event) {
        if (!readOnly && isEmpty()) {
            valueBox.setCursorPos(0);
        }
    }

    @UiHandler("valueBox")
    void onValueBoxValueChange(ChangeEvent event) {
        setValue(valueBox.getValue());
    }

    @UiHandler("valueBox")
    void onValueBoxBlur(BlurEvent event) {
        if (isEmpty()) {
            setStyleName(correctStyle);
            clear();
        }
    }

    @UiHandler("valueBox")
    void onValueBoxKeyDown(KeyDownEvent event) {
        if (!readOnly && isEmpty()) {
            valueBox.setValue(null);
            valueBox.setStyleName(filledStyle);
        }
    }

    @UiHandler("valueBox")
    void onValueBoxKeyUp(KeyUpEvent event) {

        if (valueBox.getValue() != null
                && valueBox.getValue().equals(getPlaceholder())) {
            enteredValueEqualsPlaceholder = true;
        }

        if (clearButtonVisible && !clearButtonVisibleWhileEmpty)
        {
            setClearButtonVisibility(!isEmpty());
        }
    }
    //endregion UiBinder handlers

    //region PlaceHolder
    @Override
    public T getPlaceholder() {
        return placeHolder;
    }

    @Override
    public void setPlaceholder(T newPlaceHolder) {

        if (isEmpty()) {
            valueBox.setValue(newPlaceHolder);
            enteredValueEqualsPlaceholder = false;
        }

        this.placeHolder = newPlaceHolder;

    }
    //endregion PlaceHolder

    //region HasEnabled
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        valueBox.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        if (!enabled) {
            setStyleName(disabledStyle);
        } else {
            if (isCorrect(getValue())) {
                setStyleName(correctStyle);
            }  else {
                setStyleName(incorrectStyle);
                valueBox.setStyleName(incorrectValueBoxStyle);
            }
        }
    }
    //endregion HasEnabled

    //region HasValue<T>
    @Override
    public T getValue() {
        boolean placeholderIsInBox = valueBox.getValue() != null
                && !enteredValueEqualsPlaceholder
                && valueBox.getValue().equals(getPlaceholder());
        if (placeholderIsInBox) {
            return null;
        } else {
            return valueBox.getValue();
        }
    }

    @Override
    public void setValue(T value) {
        setValue(value, true);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }

        if (value != null && value.equals(getPlaceholder())) {
            enteredValueEqualsPlaceholder = true;
        } else {
            enteredValueEqualsPlaceholder = false;
        }

        if (value == null || value.equals("")) {
            setStyleName(correctStyle);
            clear();
        } else {
            T newValue = preprocessValue(value);
            valueBox.setValue(newValue);

            if (isCorrect(newValue)) {
                setStyleName(correctStyle);
                valueBox.setStyleName(filledStyle);
            } else {
                setStyleName(incorrectStyle);
                valueBox.setStyleName(incorrectValueBoxStyle);
            }

            if (clearButtonVisible) {
                setClearButtonVisibility(true);
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return valueBox.addValueChangeHandler(handler);
    }
    //endregion HasValue<T>

    //region Behavior setters
    @Override
    public void setClearButtonVisible(final boolean visible) {
        this.clearButtonVisible = visible;
        if (clearButtonVisibleWhileEmpty || !isEmpty()) {
            setClearButtonVisibility(clearButtonVisible);
        }
    }

    @Override
    public void setClearButtonVisibleWhileEmpty(final boolean clearButtonVisibleWhileEmpty) {
        this.clearButtonVisibleWhileEmpty = clearButtonVisibleWhileEmpty;
        if (!clearButtonVisibleWhileEmpty) {
            setClearButtonVisibility(!isEmpty());
        } else {
            setClearButtonVisibility(true);
        }
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        valueBox.setReadOnly(readOnly);
        clearButton.setEnabled(!readOnly);
        clearButton.setEnabled(!readOnly);
        if (readOnly) {
            valueBox.addStyleName(readOnlyStyle);
        } else {
            valueBox.removeStyleName(readOnlyStyle);
        }
    }

    //region HasPreprocess
    @Override
    public PreprocessAlgorithm<T> getPreprocessAlgorithm() {
        return preprocessAlgorithm;
    }

    @Override
    public void setPreprocessAlgorithm(final PreprocessAlgorithm<T> preprocessAlgorithm) {
        this.preprocessAlgorithm = preprocessAlgorithm;
        if (!isEmpty()) {
            setValue(preprocessValue(getValue()));
        }
    }
    //endregion HasPreprocess

    //region HasValidation
    @Override
    public ValidationAlgorithm<T> getValidationAlgorithm() {
        return validationAlgorithm;
    }

    @Override
    public void setValidationAlgorithm(final ValidationAlgorithm<T> validationAlgorithm) {
        this.validationAlgorithm = validationAlgorithm;
        if (!isEmpty()) {
            if (isCorrect(getValue())) {
                setStyleName(correctStyle);
                valueBox.setStyleName(filledStyle);
            } else {
                setStyleName(incorrectStyle);
                valueBox.setStyleName(incorrectValueBoxStyle);
            }
        }
    }
    //endregion HasValidation

    //endregion Behavior setters

    //region Clear logic
    public void clear() {

        enteredValueEqualsPlaceholder = false;

        if (!clearButtonVisibleWhileEmpty) {
            setClearButtonVisibility(false);
        }

        T oldValue = valueBox.getValue();

        valueBox.setValue(getPlaceholder());
        valueBox.setStyleName(emptyStyle);

        ClearEvent.fire(this, oldValue);
    }

    @Override
    public HandlerRegistration addClearHandler(final ClearEvent.Handler<T> handler) {
        return addHandler(handler, ClearEvent.TYPE);
    }
    //endregion Clear logic

    //region Style
    @Override
    public String getStyleNameValueBox() {
        return valueBox.getStyleName();
    }

    @Override
    public void setStyleEmptyValueBox(final String style) {
        if (!emptyStyle.equals(style)) {
            emptyStyle = style;
            if (isEmpty()) {
                valueBox.setStyleName(emptyStyle);
            }
        }
    }

    @Override
    public String getStyleEmptyValueBox() {
        return emptyStyle;
    }

    @Override
    public void setStyleIncorrectValueBox(final String style) {
        if (!incorrectValueBoxStyle.equals(style)) {
            incorrectValueBoxStyle = style;
            if (!isCorrect(getValue())) {
                valueBox.setStyleName(incorrectValueBoxStyle);
            }
        }
    }

    @Override
    public String getStyleIncorrectValueBox() {
        return incorrectValueBoxStyle;
    }

    @Override
    public void setStyleFilledValueBox(final String style) {
        if (!filledStyle.equals(style)) {
            filledStyle = style;
            if (!isEmpty()) {
                valueBox.setStyleName(filledStyle);
            }
        }
    }

    @Override
    public String getStyleFilledValueBox() {
        return filledStyle;
    }

    @Override
    public String getStyleReadOnlyValueBox() {
        return readOnlyStyle;
    }

    @Override
    public void setStyleReadOnlyValueBox(final String style) {
        if (valueBox.isReadOnly()) {
            valueBox.removeStyleName(readOnlyStyle);
            valueBox.addStyleName(style);
        }
        readOnlyStyle = style;
    }

    @Override
    public void setStyleIncorrect(final String style) {
        if(!incorrectStyle.equals(style)) {
            incorrectStyle = style;
            if (isEnabled() && !isCorrect(getValue())) {
                setStyleName(incorrectStyle);
            }
        }
    }

    @Override
    public String getStyleIncorrect() {
        return incorrectStyle;
    }

    @Override
    public void setStyleCorrect(final String style) {
        if (!correctStyle.equals(style)) {
            correctStyle = style;
            if (isEnabled() && isCorrect(getValue())) {
                setStyleName(correctStyle);
            }
        }
    }

    @Override
    public String getStyleCorrect() {
        return correctStyle;
    }

    @Override
    public void setStyleDisabled(final String style) {
        if (!disabledStyle.equals(style)) {
            disabledStyle = style;
            if(!isEnabled()) {
                setStyleName(disabledStyle);
            }
        }
    }

    @Override
    public String getStyleDisabled() {
        return disabledStyle;
    }
    //endregion

    @Override
    public boolean isEmpty() {
        if (enteredValueEqualsPlaceholder) {
            return false;
        }
        return getValue() == null
                || getValue().equals("") // dirty hack for TextBox
                || getValue().equals(getPlaceholder());
    }

    private void setClearButtonVisibility(final boolean visible) {

        if (visible) {
            clearButton.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            clearButton.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
    }

    private boolean isCorrect(T value) {
        if (validationAlgorithm != null) {
            return validationAlgorithm.validate(value);
        } else {
            return true;
        }
    }

    private T preprocessValue(T value) {
        if (preprocessAlgorithm != null) {
            return preprocessAlgorithm.preprocess(value);
        } else {
            return value;
        }
    }

}