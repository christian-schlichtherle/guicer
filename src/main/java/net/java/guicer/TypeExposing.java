/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.*;

/**
 * A declaration of an exposing of a binding for a type in a
 * {@link PrivateModule}.
 *
 * @param  <Type> The type of the bindable type.
 * @param  <Parent> the type of the parent scope.
 * @author Christian Schlichtherle
 */
public abstract class TypeExposing<Type, Parent>
extends Bindable<TypeExposing<Type, Parent>, Type>
implements Injection<Parent> {

    void installTo(final PrivateBinder binder) {
        if (null == annotation) binder.expose(type);
        else binder.expose(type).annotatedWith(annotation);
    }
}
