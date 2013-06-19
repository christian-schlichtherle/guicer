/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

/**
 * A configuration item which is bindable to a type.
 *
 * @param  <This> the type of {@code this} when returned from a chainable
 *         method call.
 *         Set this to the sub-class which you want to use with method chaining.
 * @param  <Type> the type of the bindable type.
 * @author Christian Schlichtherle
 */
@SuppressWarnings("PackageVisibleField")
public abstract class Bindable<This extends Bindable<This, Type>, Type>
extends Annotatable<This> {

    Class<Type> type;

    @SuppressWarnings("unchecked")
    This bind(final Class<Type> type) {
        this.type = type;
        return (This) this;
    }
}
