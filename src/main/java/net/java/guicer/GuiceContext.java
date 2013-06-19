/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.Injector;

/**
 * Provides an alternative, simple Domain Specific Language (DSL) for
 * configuring a Guice {@link Injector}.
 *
 * @author Christian Schlichtherle
 */
public class GuiceContext {
    public final InjectorBuilder injector() { return new InjectorBuilder(); }
}
