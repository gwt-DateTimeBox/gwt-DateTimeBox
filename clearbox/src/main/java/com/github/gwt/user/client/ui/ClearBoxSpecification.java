package com.github.gwt.user.client.ui;/*
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

import com.github.gwt.user.client.ui.events.HasClearingHandlers;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Specification of ClearBox. Assumes that it is composite widget,
 * containing some {@link com.google.gwt.user.client.ui.ValueBoxBase} input box
 * and some {@link com.google.gwt.user.client.ui.FocusWidget} clear button.
 *
 * @param <T> the type of value in box
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public interface ClearBoxSpecification<T> extends IsWidget,
                                                  HasValue<T>,
                                                  HasPlaceholder<T>,
                                                  HasValidation<T>,
                                                  HasPreprocess<T>,
                                                  HasEnabled,
                                                  HasReadOnly,
                                                  HasClearingHandlers<T>,
                                                  ClearBoxStyles,
                                                  ValueBoxStyles {
    /**
     * Returns <tt>true</tt> if value was set. Placeholder is <b>NOT</b> a
     * value, so even if placeholder was set - returns <code>true</code>
     * until value was entered.
     *
     * @return <code>true</code> if value is not set or was cleared.
     *         While displaying placeholder also returns <code>true</code>.
     *         <code>false/code> if value was set.
     */
    boolean isEmpty();

    /**
     * Configure visibility of clearing button, <b>NOT</b> the whole widget.
     * Widget width remains the same.
     *
     * @param clearButtonVisible if <code>true</code>, the clear button becomes
     *                           visible;
     *                           if <code>false</code>, the clear button becomes
     *                           invisible.
     */
    void setClearButtonVisible(boolean clearButtonVisible);

    /**
     * Configure visibility of clear button while value box is empty.
     *
     * @param clearButtonVisibleWhileEmpty if <code>true</code>, the clear
     *                                     button remains visible while value
     *                                     box is empty;
     *                                     if <code>false</code>, the clear
     *                                     button becomes invisible while value
     *                                     box is empty (clear button hides on
     *                                     clearing box).
     */
    void setClearButtonVisibleWhileEmpty(boolean clearButtonVisibleWhileEmpty);

}