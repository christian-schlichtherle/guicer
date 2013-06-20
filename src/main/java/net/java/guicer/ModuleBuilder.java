/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.*;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.AnnotatedElementBuilder;
import com.google.inject.binder.ConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;
import static net.java.guicer.ModuleContainer.emptyList;

/**
 * A builder for a {@link Module}.
 *
 * @param  <Parent> the type of the parent scope.
 * @author Christian Schlichtherle
 */
public abstract class ModuleBuilder<Parent>
extends ModuleContainer<ModuleBuilder<Parent>>
implements Builder<Module>, Injection<Parent> {

    private List<Configuration<PrivateBinder>> exposings = emptyList();
    private List<Configuration<Binder>> bindings = emptyList();

    public <Type> AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> exposeAndBind(
            final Class<Type> clazz) {
        throw new UnsupportedOperationException();
    }

    public AnnotatedElementBuilderWithInjection<ModuleBuilder<Parent>> expose(
            final Class<?> clazz) {
        return new AnnotatedElementConfiguration() {
            @Override public AnnotatedElementBuilder configure(PrivateBinder binder) {
                return binder.expose(clazz);
            }
        };
    }

    ModuleBuilder<Parent> addExposing(
            final Configuration<PrivateBinder> exposing) {
        exposings.add(exposing);
        return this;
    }

    public <Type> AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> bind(
            final Class<Type> clazz) {
        return new AnnotatedBindingConfiguration<Type>() {
            @Override public AnnotatedBindingBuilder<Type> configure(Binder binder) {
                return binder.bind(clazz);
            }
        };
    }

    public AnnotatedConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> bindConstant() {
        return new AnnotatedConstantBindingConfiguration() {
            @Override public AnnotatedConstantBindingBuilder configure(Binder binder) {
                return binder.bindConstant();
            }
        };
    }

    ModuleBuilder<Parent> addBinding(final Configuration<Binder> binding) {
        bindings.add(binding);
        return this;
    }

    @Override public Module build() {
        final List<Configuration<PrivateBinder>> exposings = swapExposings();
        if (exposings.isEmpty()) {
            return new AbstractModule() {
                @Override protected void configure() { installTo(binder()); }
            };
        } else {
            return new PrivateModule() {
                @Override protected void configure() {
                    final PrivateBinder binder = binder();
                    for (Configuration<PrivateBinder> exposing : exposings)
                        exposing.configure(binder);
                    installTo(binder);
                }
            };
        }
    }

    void installTo(final Binder binder) {
        for (Configuration<Binder> binding : swapBindings())
            binding.configure(binder);
        for (Module module : swapModules())
            binder.install(module);
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Configuration<PrivateBinder>> swapExposings() {
        try { return this.exposings; }
        finally { this.exposings = emptyList(); }
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Configuration<Binder>> swapBindings() {
        try { return this.bindings; }
        finally { this.bindings = emptyList(); }
    }

    private abstract class AnnotatedElementConfiguration
    extends ExposingConfiguration
    implements AnnotatedElementBuilderWithInjection<ModuleBuilder<Parent>> {
        @Override public abstract AnnotatedElementBuilder configure(PrivateBinder binder);

        @Override public final Injection<ModuleBuilder<Parent>> annotatedWith(
                final Class<? extends Annotation> annotationType) {
            return new ExposingConfiguration() {
                @Override public Void configure(PrivateBinder binder) {
                    AnnotatedElementConfiguration.this.configure(binder).annotatedWith(annotationType);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new ExposingConfiguration() {
                @Override public Void configure(PrivateBinder binder) {
                    AnnotatedElementConfiguration.this.configure(binder).annotatedWith(annotation);
                    return null;
                }
            };
        }
    }

    private abstract class ExposingConfiguration
    implements Injection<ModuleBuilder<Parent>>, Configuration<PrivateBinder> {
        @Override public final ModuleBuilder<Parent> inject() {
            return addExposing(this);
        }
    }

    private abstract class AnnotatedConstantBindingConfiguration
    extends BindingConfiguration
    implements AnnotatedConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> {
        @Override public abstract AnnotatedConstantBindingBuilder configure(Binder binder);

        @Override public final ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> annotatedWith(
            final Class<? extends Annotation> annotationType) {
            return new ConstantBindingConfiguration() {
                @Override public ConstantBindingBuilder configure(Binder binder) {
                    return AnnotatedConstantBindingConfiguration.this.configure(binder).annotatedWith(annotationType);
                }
            };
        }

        @Override public final ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new ConstantBindingConfiguration() {
                @Override public ConstantBindingBuilder configure(Binder binder) {
                    return AnnotatedConstantBindingConfiguration.this.configure(binder).annotatedWith(annotation);
                }
            };
        }
    }

    private abstract class ConstantBindingConfiguration
    extends BindingConfiguration
    implements ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> {
        @Override public abstract ConstantBindingBuilder configure(Binder binder);

        @Override public final Injection<ModuleBuilder<Parent>> to(final String value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final int value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final long value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final boolean value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final double value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final float value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final short value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final char value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final byte value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final Class<?> value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final <E extends Enum<E>> Injection<ModuleBuilder<Parent>> to(final E value) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ConstantBindingConfiguration.this.configure(binder).to(value);
                    return null;
                }
            };
        }
    }

    private abstract class AnnotatedBindingConfiguration<Type>
    extends LinkedBindingConfiguration<Type>
    implements AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> {
        @Override public abstract AnnotatedBindingBuilder<Type> configure(Binder binder);

        @Override
        public final LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> annotatedWith(
                final Class<? extends Annotation> annotationType) {
            return new LinkedBindingConfiguration<Type>() {
                @Override public LinkedBindingBuilder<Type> configure(Binder input) {
                    return AnnotatedBindingConfiguration.this.configure(input).annotatedWith(annotationType);
                }
            };
        }

        @Override
        public final LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new LinkedBindingConfiguration<Type>() {
                @Override public LinkedBindingBuilder<Type> configure(Binder input) {
                    return AnnotatedBindingConfiguration.this.configure(input).annotatedWith(annotation);
                }
            };
        }
    }

    private abstract class LinkedBindingConfiguration<Type>
    extends ScopedBindingConfiguration
    implements LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> {
        @Override public abstract LinkedBindingBuilder<Type> configure(Binder binder);

        @Override public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final Class<? extends Type> implementation) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).to(implementation);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final TypeLiteral<? extends Type> implementation) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).to(implementation);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final Key<? extends Type> targetKey) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).to(targetKey);
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> toInstance(
                final Type instance) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    LinkedBindingConfiguration.this.configure(binder).toInstance(instance);
                    return null;
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Provider<? extends Type> provider) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).toProvider(provider);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Class<? extends javax.inject.Provider<? extends Type>> providerType) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).toProvider(providerType);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final TypeLiteral<? extends javax.inject.Provider<? extends Type>> providerType) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).toProvider(providerType);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Key<? extends javax.inject.Provider<? extends Type>> providerKey) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).toProvider(providerKey);
                }
            };
        }

        @Override
        public final <S extends Type> ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toConstructor(
                final Constructor<S> constructor) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).toConstructor(constructor);
                }
            };
        }

        @Override
        public final <S extends Type> ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toConstructor(
                final Constructor<S> constructor,
                final TypeLiteral<? extends S> type) {
            return new ScopedBindingConfiguration() {
                @Override public ScopedBindingBuilder configure(Binder binder) {
                    return LinkedBindingConfiguration.this.configure(binder).toConstructor(constructor, type);
                }
            };
        }
    }

    private abstract class ScopedBindingConfiguration
    extends BindingConfiguration
    implements ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> {
        @Override public abstract ScopedBindingBuilder configure(Binder binder);

        @Override
        public final Injection<ModuleBuilder<Parent>> in(
                final Class<? extends Annotation> scopeAnnotation) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ScopedBindingConfiguration.this.configure(binder).in(scopeAnnotation);
                    return null;
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> in(final Scope scope) {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ScopedBindingConfiguration.this.configure(binder).in(scope);
                    return null;
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> asEagerSingleton() {
            return new BindingConfiguration() {
                @Override public Void configure(Binder binder) {
                    ScopedBindingConfiguration.this.configure(binder).asEagerSingleton();
                    return null;
                }
            };
        }
    }

    private abstract class BindingConfiguration
    implements Injection<ModuleBuilder<Parent>>, Configuration<Binder> {
        @Override public final ModuleBuilder<Parent> inject() {
            return addBinding(this);
        }
    }
}
