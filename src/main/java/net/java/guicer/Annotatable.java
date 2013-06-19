/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import java.lang.annotation.Annotation;

/**
 * A configuration item which is annotatable.
 *
 * @param  <This> the type of {@code this} when returned from a chainable
 *         method call.
 *         Set this to the sub-class which you want to use with method chaining.
 * @author Christian Schlichtherle
 */
@SuppressWarnings("PackageVisibleField")
public abstract class Annotatable<This extends Annotatable<This>> {

    Annotation annotation;

    Annotatable() { }

    @SuppressWarnings("unchecked")
    public This annotatedWith(final Annotation annotation) {
        this.annotation = annotation;
        return (This) this;
    }
}
