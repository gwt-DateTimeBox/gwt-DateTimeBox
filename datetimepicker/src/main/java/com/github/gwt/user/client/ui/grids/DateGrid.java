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

import com.github.gwt.user.client.ui.HasSelectedValue;
import com.github.gwt.user.client.ui.HasStateValue;
import com.github.gwt.user.client.ui.ValueChain;
import com.github.gwt.user.client.ui.events.HasBackHandlers;
import com.github.gwt.user.client.ui.events.HasNextHandlers;
import com.github.gwt.user.client.ui.events.HasStateChangedHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

/**
 * Grid with back and next buttons, changing state and selection.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public interface DateGrid extends IsWidget,
        HasNextHandlers,
        HasBackHandlers,
        ValueChain<Date>,
        HasStateValue<Date>,
        HasSelectedValue<Date>,
        HasStateChangedHandlers<Date>,
        HasSelectionHandlers<Date> {
}
