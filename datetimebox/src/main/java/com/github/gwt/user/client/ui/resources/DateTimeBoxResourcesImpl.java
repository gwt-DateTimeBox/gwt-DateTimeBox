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

package com.github.gwt.user.client.ui.resources;

import com.google.gwt.core.client.GWT;

/**
 * {@link com.github.gwt.user.client.ui.DateTimeBox} resources.
 * In order to give the possibility to client to
 * replace the whole resources bundle in gwt.xml file (replace-with),
 * resources are moved to "stub" and are instantiated by GWT.create() method.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public class DateTimeBoxResourcesImpl {

    /**
     * In order to give the possibility to change resources in gwt.xml.
     *
     * <replace-with class="custom.resources.ResourcesImpl">
     *     <when-type-is class=com.github.gwt.user.client.ui.resources
     *     .DateTimeBoxResourcesImpl"/>
     * </replace-with>
     */
    public static final DateTimeBoxResourcesStub impl
            = GWT.create(DateTimeBoxResourcesStub.class);

    public static DateTimeBoxResources dateTimeBoxResources() {
        return impl.getDateTimeBoxResources();
    }

}
