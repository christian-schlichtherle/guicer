/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.*;
import java.lang.annotation.Annotation;

/**
 * A declaration of a binding for a type in a {@link Module}.
 *
 * @param  <Type> The type of the bindable type.
 * @param  <Parent> the type of the parent scope.
 * @author Christian Schlichtherle
 */
@SuppressWarnings("PackageVisibleField")
public abstract class TypeBinding<Type, Parent>
extends Bindable<TypeBinding<Type, Parent>, Type>
implements Injection<Parent> {

    private Class<? extends Type> implementation;
    private Type instance;
    private Class<? extends Annotation> scopeAnnotation;

    public TypeBinding<Type, Parent> to(final Class<? extends Type> implementation) {
        this.implementation = implementation;
        return this;
    }

    public TypeBinding<Type, Parent> toInstance(final Type instance) {
        this.instance = instance;
        return this;
    }

    public TypeBinding<Type, Parent> in(final Class<? extends Annotation> scopeAnnotation) {
        this.scopeAnnotation = scopeAnnotation;
        return this;
    }

    void installTo(final Binder binder) {
        if (null == instance) {
            if (null == annotation) {
                if (null == scopeAnnotation) {
                    binder  .bind(type)
                            .to(implementation);
                } else {
                    binder  .bind(type)
                            .to(implementation)
                            .in(scopeAnnotation);
                }
            } else {
                if (null == scopeAnnotation) {
                    binder  .bind(type)
                            .annotatedWith(annotation)
                            .to(implementation);
                } else {
                    binder  .bind(type)
                            .annotatedWith(annotation)
                            .to(implementation)
                            .in(scopeAnnotation);
                }
            }
        } else {
            if (null != implementation) throw new IllegalStateException();
            if (null != scopeAnnotation) throw new IllegalStateException();
            if (null == annotation) {
                binder  .bind(type)
                        .toInstance(instance);
            } else {
                binder  .bind(type)
                        .annotatedWith(annotation)
                        .toInstance(instance);
            }
        }
    }
}
