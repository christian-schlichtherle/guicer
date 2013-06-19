/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.Module;
import java.util.*;

/**
 * A container for modules.
 *
 * @param  <This> The type of {@code this} when returned from a chainable
 *         method call.
 *         Set this to the sub-class which you want to use with method chaining.
 * @author Christian Schlichtherle
 */
public abstract class ModuleContainer<This extends ModuleContainer<This>> {

    private List<Module> modules = emptyList();

    ModuleContainer() { }

    static <T> List<T> emptyList() { return new LinkedList<T>(); }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Module> swapModules() {
        try { return this.modules; }
        finally { this.modules = emptyList(); }
    }

    public ModuleBuilder<This> module() {
        return new ModuleBuilder<This>() {
            @Override public This inject() {
                return ModuleContainer.this.module(build());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public This module(final Module module) {
        modules.add(module);
        return (This) this;
    }
}
