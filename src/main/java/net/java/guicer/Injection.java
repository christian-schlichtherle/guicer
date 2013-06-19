/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

/**
 * Injects a dependency into some target.
 *
 * @param  <Parent> the type of the parent scope.
 * @author Christian Schlichtherle
 */
public interface Injection<Parent> {
    /** Injects the dependency into the parent scope and returns control to it. */
    Parent inject();
}
