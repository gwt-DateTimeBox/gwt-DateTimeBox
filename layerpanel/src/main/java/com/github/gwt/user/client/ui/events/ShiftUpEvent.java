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

package com.github.gwt.user.client.ui.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents shift up event.
 *
 * @author Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version %I%, %G%
 * @since 1.0
 */
public class ShiftUpEvent<T> extends GwtEvent<ShiftUpEvent.Handler<T>> {
    public static Type<Handler<?>> TYPE = new Type<Handler<?>>();

    public interface Handler<T> extends EventHandler {
        void onEvent(ShiftUpEvent<T> event);
    }

    public T getInfo() {
        return info;
    }

    private final T info;

    protected ShiftUpEvent(T info) {
        this.info = info;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(Handler<T> handler) {
        handler.onEvent(this);
    }

    public static <T> void fire(HasShiftUpHandlers<T> source) {
        if (TYPE != null) {
            ShiftUpEvent<T> event = new ShiftUpEvent<T>(null);
            source.fireEvent(event);
        }
    }

    public static <T> void fire(HasShiftUpHandlers<T> source, T info) {
        if (TYPE != null) {
            ShiftUpEvent<T> event = new ShiftUpEvent<T>(info);
            source.fireEvent(event);
        }
    }
}