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


/**
 * Signals that implementer has value that is currently selected.
 *
 * @param <T> the type of selected value
 *
 * @author      Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version     %I%, %G%
 * @since       1.0
 */
public interface HasSelectedValue<T> {

    /**
     * @return currently selected value.
     */
    public T getSelectedValue();

    /**
     * Sets currently selected value.
     *
     * @param value to be set as selected.
     */
    public void setSelectedValue(T value);
}
