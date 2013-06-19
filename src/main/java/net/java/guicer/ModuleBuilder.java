/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.*;
import static net.java.guicer.ModuleContainer.emptyList;
import java.util.List;

/**
 * A builder for a {@link Module}.
 *
 * @param  <Parent> the type of the parent scope.
 * @author Christian Schlichtherle
 */
public abstract class ModuleBuilder<Parent>
extends ModuleContainer<ModuleBuilder<Parent>>
implements Builder<Module>, Injection<Parent> {

    private List<Installable<PrivateBinder>> exposings = emptyList();
    private List<Installable<Binder>> bindings = emptyList();

    public <Type> TypeBinding<Type, ModuleBuilder<Parent>> exposeAndBind(
            Class<Type> clazz) {
        return new InstallableTypeExposingAndBinding<Type>().bind(clazz);
    }

    private class InstallableTypeExposingAndBinding<Type>
    extends TypeExposingAndBinding<Type, ModuleBuilder<Parent>>
    implements Installable<PrivateBinder> { // not part of the DSL!
        @Override public ModuleBuilder<Parent> inject() {
            return addExposing(this);
        }

        @Override public void installTo(PrivateBinder binder) {
            super.installTo(binder);
        }
    }

    public <Type> TypeExposing<Type, ModuleBuilder<Parent>> expose(
            Class<Type> clazz) {
        return new InstallableTypeExposing<Type>().bind(clazz);
    }

    private class InstallableTypeExposing<Type>
    extends TypeExposing<Type, ModuleBuilder<Parent>>
    implements Installable<PrivateBinder> { // not part of the DSL!
        @Override public ModuleBuilder<Parent> inject() {
            return addExposing(this);
        }

        @Override public void installTo(PrivateBinder binder) {
            super.installTo(binder);
        }
    }

    ModuleBuilder<Parent> addExposing(
            final Installable<PrivateBinder> exposing) {
        exposings.add(exposing);
        return this;
    }

    public <Type> TypeBinding<Type, ModuleBuilder<Parent>> bind(
            Class<Type> clazz) {
        return new InstallableTypeBinding<Type>().bind(clazz);
    }

    private class InstallableTypeBinding<Type>
    extends TypeBinding<Type, ModuleBuilder<Parent>>
    implements Installable<Binder> { // not part of the DSL!
        @Override public ModuleBuilder<Parent> inject() {
            return addBinding(this);
        }

        @Override public void installTo(Binder binder) {
            super.installTo(binder);
        }
    }

    public <Type> ConstantBinding<Type, ModuleBuilder<Parent>> bindConstant() {
        return new InstallableConstantBinding<Type>();
    }

    private class InstallableConstantBinding<Type>
    extends ConstantBinding<Type, ModuleBuilder<Parent>>
    implements Installable<Binder> { // not part of the DSL!
        @Override public ModuleBuilder<Parent> inject() {
            return addBinding(this);
        }

        @Override public void installTo(Binder binder) {
            super.installTo(binder);
        }
    }

    ModuleBuilder<Parent> addBinding(final Installable<Binder> binding) {
        bindings.add(binding);
        return this;
    }

    @Override public Module build() {
        final List<Installable<PrivateBinder>> exposings = swapExposings();
        if (exposings.isEmpty()) {
            return new AbstractModule() {
                @Override protected void configure() { installTo(binder()); }
            };
        } else {
            return new PrivateModule() {
                @Override protected void configure() {
                    final PrivateBinder binder = binder();
                    for (Installable<PrivateBinder> exposing : exposings)
                        exposing.installTo(binder);
                    installTo(binder);
                }
            };
        }
    }

    void installTo(final Binder binder) {
        for (Installable<Binder> binding : swapBindings())
            binding.installTo(binder);
        for (Module module : swapModules())
            binder.install(module);
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Installable<PrivateBinder>> swapExposings() {
        try { return this.exposings; }
        finally { this.exposings = emptyList(); }
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Installable<Binder>> swapBindings() {
        try { return this.bindings; }
        finally { this.bindings = emptyList(); }
    }
}
