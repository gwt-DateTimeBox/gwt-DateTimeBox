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

package com.github.gwt.user.client.ui.events.appear.did;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents something did appear event.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public class DidAppearEvent<T, K> extends GwtEvent<DidAppearHandler<T, K>> {
    public static Type<DidAppearHandler<?, ?>> TYPE = new Type<DidAppearHandler<?, ?>>();

    public K getPresented() {
        return presented;
    }

    private final K presented;

    protected DidAppearEvent(K presented) {
        this.presented = presented;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Type<DidAppearHandler<T, K>> getAssociatedType() {
        return (Type) TYPE;
    }


    @Override
    protected void dispatch(DidAppearHandler<T, K> handler) {
        handler.onDidPresent(this);
    }

    public static <T, K> void fire(HasDidAppearHandlers<T, K> source, K presented) {
        if (TYPE != null) {
            DidAppearEvent<T, K> event = new DidAppearEvent<T, K>(presented);
            source.fireEvent(event);
        }
    }
}