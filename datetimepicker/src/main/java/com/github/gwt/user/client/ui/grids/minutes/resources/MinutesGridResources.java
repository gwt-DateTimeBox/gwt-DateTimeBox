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

package com.github.gwt.user.client.ui.grids.minutes.resources;

import com.github.gwt.user.client.ui.grids.resources.DateGridResources;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Resources for {@link com.github.gwt.user.client.ui.grids.days.DaysGrid}.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public interface MinutesGridResources extends DateGridResources {

    @ClientBundle.Source("com/github/gwt/user/client/ui/css/grids/MinutesGrid.css")
    MinutesGridStyle style();

    public interface MinutesGridStyle extends DateGridStyle {
    }
}