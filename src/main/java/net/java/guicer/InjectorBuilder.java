/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.*;

/**
 * A builder for an {@link Injector}.
 *
 * @author Christian Schlichtherle
 */
public class InjectorBuilder
extends ModuleContainer<InjectorBuilder>
implements Builder<Injector> {
    @Override public Injector build() {
        return Guice.createInjector(swapModules());
    }
}
