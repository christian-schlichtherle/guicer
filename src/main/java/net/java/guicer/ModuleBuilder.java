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

    private List<Configuration<?>> exposings = emptyList();
    private List<Configuration<?>> bindings = emptyList();

    public <Type> AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> exposeAndBind(
            final Class<Type> clazz) {
        return new AnnotatedBindingConfiguration<Type>() {
            @Override void add(Configuration<?> configuration) {
                addExposing(configuration);
                addBinding(configuration);
            }

            @Override public AnnotatedElementBuilder expose(PrivateBinder binder) {
                return binder.expose(clazz);
            }

            @Override public AnnotatedBindingBuilder<Type> bind(Binder binder) {
                return binder.bind(clazz);
            }
        };
    }

    public <Type> LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> exposeAndBind(
            final Key<Type> key) {
        return new LinkedBindingConfiguration<Type, Configuration<?>>() {
            @Override void add(Configuration<?> configuration) {
                addExposing(configuration);
                addBinding(configuration);
            }

            @Override public Void expose(PrivateBinder binder) {
                binder.expose(key);
                return null;
            }

            @Override public LinkedBindingBuilder<Type> bind(Binder binder) {
                return binder.bind(key);
            }
        };
    }

    public <Type> AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> exposeAndBind(
            final TypeLiteral<Type> literal) {
        return new AnnotatedBindingConfiguration<Type>() {
            @Override void add(Configuration<?> configuration) {
                addExposing(configuration);
                addBinding(configuration);
            }

            @Override public AnnotatedElementBuilder expose(PrivateBinder binder) {
                return binder.expose(literal);
            }

            @Override public AnnotatedBindingBuilder<Type> bind(Binder binder) {
                return binder.bind(literal);
            }
        };
    }

    public AnnotatedElementBuilderWithInjection<ModuleBuilder<Parent>> expose(
            final Class<?> clazz) {
        return new AnnotatedElementConfiguration<Configuration<?>>() {
            @Override void add(Configuration<?> configuration) {
                addExposing(configuration);
            }

            @Override public AnnotatedElementBuilder expose(PrivateBinder binder) {
                return binder.expose(clazz);
            }
        };
    }

    public Injection<ModuleBuilder<Parent>> expose(
            final Key<?> key) {
        return new Configuration<Configuration<?>>() {
            @Override void add(Configuration<?> configuration) {
                addExposing(configuration);
            }

            @Override public Void expose(PrivateBinder binder) {
                binder.expose(key);
                return null;
            }
        };
    }

    public AnnotatedElementBuilderWithInjection<ModuleBuilder<Parent>> expose(
            final TypeLiteral<?> literal) {
        return new AnnotatedElementConfiguration<Configuration<?>>() {
            @Override void add(Configuration<?> configuration) {
                addExposing(configuration);
            }

            @Override public AnnotatedElementBuilder expose(PrivateBinder binder) {
                return binder.expose(literal);
            }
        };
    }

    void addExposing(Configuration<?> exposing) { exposings.add(exposing); }

    public <Type> AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> bind(
            final Class<Type> clazz) {
        return new AnnotatedBindingConfiguration<Type>() {
            @Override void add(Configuration<?> configuration) {
                addBinding(configuration);
            }

            @Override public AnnotatedBindingBuilder<Type> bind(Binder binder) {
                return binder.bind(clazz);
            }
        };
    }

    public <Type> LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> bind(
            final Key<Type> key) {
        return new LinkedBindingConfiguration<Type, Configuration<?>>() {
            @Override void add(Configuration<?> configuration) {
                addBinding(configuration);
            }

            @Override public LinkedBindingBuilder<Type> bind(Binder binder) {
                return binder.bind(key);
            }
        };
    }

    public <Type> AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> bind(
            final TypeLiteral<Type> literal) {
        return new AnnotatedBindingConfiguration<Type>() {
            @Override void add(Configuration<?> configuration) {
                addBinding(configuration);
            }

            @Override public AnnotatedBindingBuilder<Type> bind(Binder binder) {
                return binder.bind(literal);
            }
        };
    }

    public AnnotatedConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> bindConstant() {
        return new AnnotatedConstantBindingConfiguration() {
            @Override void add(Configuration<?> configuration) {
                addBinding(configuration);
            }

            @Override public AnnotatedConstantBindingBuilder bind(Binder binder) {
                return binder.bindConstant();
            }
        };
    }

    void addBinding(Configuration<?> binding) { bindings.add(binding); }

    @Override public Module build() {
        final List<Configuration<?>> exposings = swapExposings();
        if (exposings.isEmpty()) {
            return new AbstractModule() {
                @Override protected void configure() { installTo(binder()); }
            };
        } else {
            return new PrivateModule() {
                @Override protected void configure() {
                    final PrivateBinder binder = binder();
                    for (Configuration exposing : exposings)
                        exposing.expose(binder);
                    installTo(binder);
                }
            };
        }
    }

    void installTo(final Binder binder) {
        for (Configuration<?> binding : swapBindings())
            binding.bind(binder);
        for (Module module : swapModules())
            binder.install(module);
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Configuration<?>> swapExposings() {
        try { return this.exposings; }
        finally { this.exposings = emptyList(); }
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Configuration<?>> swapBindings() {
        try { return this.bindings; }
        finally { this.bindings = emptyList(); }
    }

    private abstract class AnnotatedElementConfiguration<ConfigurationParent extends Configuration<?>>
    extends Configuration<ConfigurationParent>
    implements AnnotatedElementBuilderWithInjection<ModuleBuilder<Parent>> {

        @Override abstract AnnotatedElementBuilder expose(PrivateBinder binder);

        @Override public final Injection<ModuleBuilder<Parent>> annotatedWith(
                final Class<? extends Annotation> annotationType) {
            return new ChildConfiguration() {
                @Override Void expose(PrivateBinder binder) {
                    parentExpose(binder).annotatedWith(annotationType);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new ChildConfiguration() {
                @Override Void expose(PrivateBinder binder) {
                    parentExpose(binder).annotatedWith(annotation);
                    return null;
                }
            };
        }

        private abstract class ChildConfiguration
        extends Configuration<AnnotatedElementConfiguration<ConfigurationParent>> {
            final AnnotatedElementBuilder parentExpose(PrivateBinder binder) {
                return parent().expose(binder);
            }

            @Override final AnnotatedElementConfiguration<ConfigurationParent> parent() {
                return AnnotatedElementConfiguration.this;
            }
        }
    }

    private abstract class AnnotatedBindingConfiguration<Type>
    extends LinkedBindingConfiguration<Type, Configuration<?>>
    implements AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> {

        @Override AnnotatedElementBuilder expose(PrivateBinder binder) {
            throw new AssertionError();
        }

        @Override abstract AnnotatedBindingBuilder<Type> bind(Binder binder);

        @Override
        public final LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> annotatedWith(
                final Class<? extends Annotation> annotationType) {
            return new ChildConfiguration() {
                @Override Void expose(PrivateBinder binder) {
                    parentExpose(binder).annotatedWith(annotationType);
                    return null;
                }

                @Override LinkedBindingBuilder<Type> bind(Binder input) {
                    return parentBind(input).annotatedWith(annotationType);
                }
            };
        }

        @Override
        public final LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new ChildConfiguration() {
                @Override Void expose(PrivateBinder binder) {
                    parentExpose(binder).annotatedWith(annotation);
                    return null;
                }

                @Override LinkedBindingBuilder<Type> bind(Binder input) {
                    return parentBind(input).annotatedWith(annotation);
                }
            };
        }

        private abstract class ChildConfiguration
        extends LinkedBindingConfiguration<Type, AnnotatedBindingConfiguration<Type>> {
            final AnnotatedElementBuilder parentExpose(PrivateBinder binder) {
                return parent().expose(binder);
            }

            final AnnotatedBindingBuilder<Type> parentBind(Binder binder) {
                return parent().bind(binder);
            }

            @Override final AnnotatedBindingConfiguration<Type> parent() {
                return AnnotatedBindingConfiguration.this;
            }
        }
    }

    private abstract class LinkedBindingConfiguration<Type, ConfigurationParent extends Configuration<?>>
    extends ScopedBindingConfiguration<ConfigurationParent>
    implements LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> {

        @Override abstract LinkedBindingBuilder<Type> bind(Binder binder);

        @Override public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final Class<? extends Type> implementation) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).to(implementation);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final TypeLiteral<? extends Type> implementation) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).to(implementation);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final Key<? extends Type> targetKey) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).to(targetKey);
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> toInstance(
                final Type instance) {
            return new Configuration<LinkedBindingConfiguration<Type, ConfigurationParent>>() {
                @Override final LinkedBindingConfiguration<Type, ConfigurationParent> parent() {
                    return LinkedBindingConfiguration.this;
                }

                @Override Void expose(PrivateBinder binder) {
                    parent().expose(binder);
                    return null;
                }

                @Override Void bind(Binder binder) {
                    parent().bind(binder).toInstance(instance);
                    return null;
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Provider<? extends Type> provider) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).toProvider(provider);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Class<? extends javax.inject.Provider<? extends Type>> providerType) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).toProvider(providerType);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final TypeLiteral<? extends javax.inject.Provider<? extends Type>> providerType) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).toProvider(providerType);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Key<? extends javax.inject.Provider<? extends Type>> providerKey) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).toProvider(providerKey);
                }
            };
        }

        @Override
        public final <S extends Type> ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toConstructor(
                final Constructor<S> constructor) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).toConstructor(constructor);
                }
            };
        }

        @Override
        public final <S extends Type> ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toConstructor(
                final Constructor<S> constructor,
                final TypeLiteral<? extends S> type) {
            return new ChildConfiguration() {
                @Override ScopedBindingBuilder bind(Binder binder) {
                    return parentBind(binder).toConstructor(constructor, type);
                }
            };
        }

        private abstract class ChildConfiguration
        extends ScopedBindingConfiguration<ScopedBindingConfiguration<ConfigurationParent>> {
            @Override final Void expose(PrivateBinder binder) {
                parent().expose(binder);
                return null;
            }

            final LinkedBindingBuilder<Type> parentBind(Binder binder) {
                return parent().bind(binder);
            }

            @Override final LinkedBindingConfiguration<Type, ConfigurationParent> parent() {
                return LinkedBindingConfiguration.this;
            }
        }
    }

    private abstract class ScopedBindingConfiguration<ConfigurationParent extends Configuration<?>>
    extends Configuration<ConfigurationParent>
    implements ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> {

        @Override abstract ScopedBindingBuilder bind(Binder binder);

        @Override
        public final Injection<ModuleBuilder<Parent>> in(
                final Class<? extends Annotation> scopeAnnotation) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).in(scopeAnnotation);
                    return null;
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> in(final Scope scope) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).in(scope);
                    return null;
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> asEagerSingleton() {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).asEagerSingleton();
                    return null;
                }
            };
        }

        private abstract class ChildConfiguration
        extends Configuration<ScopedBindingConfiguration<ConfigurationParent>> {
            @Override final Void expose(PrivateBinder binder) {
                parent().expose(binder);
                return null;
            }

            final ScopedBindingBuilder parentBind(Binder binder) {
                return parent().bind(binder);
            }

            @Override final ScopedBindingConfiguration<ConfigurationParent> parent() {
                return ScopedBindingConfiguration.this;
            }
        }
    }

    private abstract class AnnotatedConstantBindingConfiguration
    extends Configuration<Configuration<?>>
    implements AnnotatedConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> {

        @Override abstract AnnotatedConstantBindingBuilder bind(Binder binder);

        @Override public final ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> annotatedWith(
            final Class<? extends Annotation> annotationType) {
            return new ChildConfiguration() {
                @Override ConstantBindingBuilder bind(Binder binder) {
                    return parentBind(binder).annotatedWith(annotationType);
                }
            };
        }

        @Override public final ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new ChildConfiguration() {
                @Override ConstantBindingBuilder bind(Binder binder) {
                    return parentBind(binder).annotatedWith(annotation);
                }
            };
        }

        private abstract class ChildConfiguration
        extends ConstantBindingConfiguration<AnnotatedConstantBindingConfiguration> {
            final AnnotatedConstantBindingBuilder parentBind(Binder binder) {
                return parent().bind(binder);
            }

            @Override final AnnotatedConstantBindingConfiguration parent() {
                return AnnotatedConstantBindingConfiguration.this;
            }
        }
    }

    private abstract class ConstantBindingConfiguration<ConfigurationParent extends Configuration<?>>
    extends Configuration<ConfigurationParent>
    implements ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> {

        @Override abstract ConstantBindingBuilder bind(Binder binder);

        @Override public final Injection<ModuleBuilder<Parent>> to(final String value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final int value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final long value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final boolean value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final double value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final float value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final short value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final char value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final byte value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final Class<?> value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final <E extends Enum<E>> Injection<ModuleBuilder<Parent>> to(final E value) {
            return new ChildConfiguration() {
                @Override Void bind(Binder binder) {
                    parentBind(binder).to(value);
                    return null;
                }
            };
        }

        private abstract class ChildConfiguration
        extends Configuration<ConstantBindingConfiguration<ConfigurationParent>> {
            final ConstantBindingBuilder parentBind(Binder binder) {
                return parent().bind(binder);
            }

            @Override final ConstantBindingConfiguration<ConfigurationParent> parent() {
                return ConstantBindingConfiguration.this;
            }
        }
    }

    private abstract class Configuration<ConfigurationParent extends Configuration<?>>
    implements Injection<ModuleBuilder<Parent>> {
        @Override public final ModuleBuilder<Parent> inject() {
            add(this);
            return ModuleBuilder.this;
        }

        void add(Configuration<?> configuration) {
            parent().add(configuration);
        }

        ConfigurationParent parent() { throw new AssertionError(); }
        Object expose(PrivateBinder binder) { throw new AssertionError(); }
        Object bind(Binder binder) { throw new AssertionError(); }
    }
}
