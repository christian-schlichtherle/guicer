/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.*;
import java.lang.annotation.Annotation;

/**
 * A combined declaration of an exposing and a binding for a type in a
 * {@link PrivateModule}.
 *
 * @param  <Type> The type of the bindable type.
 * @param  <Parent> the type of the parent scope.
 * @author Christian Schlichtherle
 */
@SuppressWarnings("PackageVisibleField")
abstract class TypeExposingAndBinding<Type, Parent>
extends TypeBinding<Type, Parent> {

    private final TypeExposing<Type, Parent>
            exposing = new TypeExposing<Type, Parent>() {
                @Override public Parent inject() { throw new AssertionError(); }
            };

    @Override TypeExposingAndBinding<Type, Parent> bind(final Class<Type> type) {
        exposing.bind(type);
        super.bind(type);
        return this;
    }

    @Override public TypeExposingAndBinding<Type, Parent> annotatedWith(final Annotation annotation) {
        exposing.annotatedWith(annotation);
        super.annotatedWith(annotation);
        return this;
    }

    void installTo(final PrivateBinder binder) {
        exposing.installTo(binder);
        super.installTo(binder);
    }
}
