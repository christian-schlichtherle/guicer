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

    private List<Configuration> exposings = emptyList();
    private List<Configuration> bindings = emptyList();

    public <Type> AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> exposeAndBind(
            final Class<Type> clazz) {
        return new AnnotatedConfiguration<Type>() {
            @Override void add(Configuration configuration) {
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

    public AnnotatedElementBuilderWithInjection<ModuleBuilder<Parent>> expose(
            final Class<?> clazz) {
        return new AnnotatedElementConfiguration() {
            @Override void add(Configuration configuration) {
                addExposing(configuration);
            }

            @Override public AnnotatedElementBuilder expose(PrivateBinder binder) {
                return binder.expose(clazz);
            }
        };
    }

    void addExposing(Configuration exposing) { exposings.add(exposing); }

    public <Type> AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> bind(
            final Class<Type> clazz) {
        return new AnnotatedConfiguration<Type>() {
            @Override void add(Configuration configuration) {
                addBinding(configuration);
            }

            @Override public AnnotatedElementBuilder expose(PrivateBinder binder) {
                throw new AssertionError();
            }

            @Override public AnnotatedBindingBuilder<Type> bind(Binder binder) {
                return binder.bind(clazz);
            }
        };
    }

    public AnnotatedConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> bindConstant() {
        return new AnnotatedConstantConfiguration() {
            @Override void add(Configuration configuration) {
                addBinding(configuration);
            }

            @Override public AnnotatedConstantBindingBuilder bind(Binder binder) {
                return binder.bindConstant();
            }
        };
    }

    void addBinding(Configuration binding) { bindings.add(binding); }

    @Override public Module build() {
        final List<Configuration> exposings = swapExposings();
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
        for (Configuration binding : swapBindings())
            binding.bind(binder);
        for (Module module : swapModules())
            binder.install(module);
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Configuration> swapExposings() {
        try { return this.exposings; }
        finally { this.exposings = emptyList(); }
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    List<Configuration> swapBindings() {
        try { return this.bindings; }
        finally { this.bindings = emptyList(); }
    }

    private abstract class AnnotatedElementConfiguration
    extends Configuration
    implements AnnotatedElementBuilderWithInjection<ModuleBuilder<Parent>> {
        @Override abstract AnnotatedElementBuilder expose(PrivateBinder binder);

        @Override public final Injection<ModuleBuilder<Parent>> annotatedWith(
                final Class<? extends Annotation> annotationType) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    AnnotatedElementConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    AnnotatedElementConfiguration.this.expose(binder).annotatedWith(annotationType);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    AnnotatedElementConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    AnnotatedElementConfiguration.this.expose(binder).annotatedWith(annotation);
                    return null;
                }
            };
        }
    }

    private abstract class AnnotatedConfiguration<Type>
    extends LinkedConfiguration<Type>
    implements AnnotatedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> {
        @Override abstract AnnotatedElementBuilder expose(PrivateBinder binder);
        @Override abstract AnnotatedBindingBuilder<Type> bind(Binder binder);

        @Override
        public final LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> annotatedWith(
                final Class<? extends Annotation> annotationType) {
            return new LinkedConfiguration<Type>() {
                @Override void add(Configuration configuration) {
                    AnnotatedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    AnnotatedConfiguration.this.expose(binder).annotatedWith(annotationType);
                    return null;
                }

                @Override LinkedBindingBuilder<Type> bind(Binder input) {
                    return AnnotatedConfiguration.this.bind(input).annotatedWith(annotationType);
                }
            };
        }

        @Override
        public final LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new LinkedConfiguration<Type>() {
                @Override void add(Configuration configuration) {
                    AnnotatedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    AnnotatedConfiguration.this.expose(binder).annotatedWith(annotation);
                    return null;
                }

                @Override LinkedBindingBuilder<Type> bind(Binder input) {
                    return AnnotatedConfiguration.this.bind(input).annotatedWith(annotation);
                }
            };
        }
    }

    private abstract class LinkedConfiguration<Type>
    extends ScopedConfiguration
    implements LinkedBindingBuilderWithInjection<Type, ModuleBuilder<Parent>> {
        @Override abstract LinkedBindingBuilder<Type> bind(Binder binder);

        @Override public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final Class<? extends Type> implementation) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).to(implementation);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final TypeLiteral<? extends Type> implementation) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).to(implementation);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> to(
                final Key<? extends Type> targetKey) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).to(targetKey);
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> toInstance(
                final Type instance) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override Void bind(Binder binder) {
                    LinkedConfiguration.this.bind(binder).toInstance(instance);
                    return null;
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Provider<? extends Type> provider) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).toProvider(provider);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Class<? extends javax.inject.Provider<? extends Type>> providerType) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).toProvider(providerType);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final TypeLiteral<? extends javax.inject.Provider<? extends Type>> providerType) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).toProvider(providerType);
                }
            };
        }

        @Override
        public final ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toProvider(
                final Key<? extends javax.inject.Provider<? extends Type>> providerKey) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).toProvider(providerKey);
                }
            };
        }

        @Override
        public final <S extends Type> ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toConstructor(
                final Constructor<S> constructor) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).toConstructor(constructor);
                }
            };
        }

        @Override
        public final <S extends Type> ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> toConstructor(
                final Constructor<S> constructor,
                final TypeLiteral<? extends S> type) {
            return new ScopedConfiguration() {
                @Override void add(Configuration configuration) {
                    LinkedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    LinkedConfiguration.this.expose(binder);
                    return null;
                }

                @Override ScopedBindingBuilder bind(Binder binder) {
                    return LinkedConfiguration.this.bind(binder).toConstructor(constructor, type);
                }
            };
        }
    }

    private abstract class ScopedConfiguration
    extends Configuration
    implements ScopedBindingBuilderWithInjection<ModuleBuilder<Parent>> {
        @Override abstract ScopedBindingBuilder bind(Binder binder);

        @Override
        public final Injection<ModuleBuilder<Parent>> in(
                final Class<? extends Annotation> scopeAnnotation) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ScopedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    ScopedConfiguration.this.expose(binder);
                    return null;
                }

                @Override Void bind(Binder binder) {
                    ScopedConfiguration.this.bind(binder).in(scopeAnnotation);
                    return null;
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> in(final Scope scope) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ScopedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    ScopedConfiguration.this.expose(binder);
                    return null;
                }

                @Override Void bind(Binder binder) {
                    ScopedConfiguration.this.bind(binder).in(scope);
                    return null;
                }
            };
        }

        @Override
        public final Injection<ModuleBuilder<Parent>> asEagerSingleton() {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ScopedConfiguration.this.add(configuration);
                }

                @Override Void expose(PrivateBinder binder) {
                    ScopedConfiguration.this.expose(binder);
                    return null;
                }

                @Override Void bind(Binder binder) {
                    ScopedConfiguration.this.bind(binder).asEagerSingleton();
                    return null;
                }
            };
        }
    }

    private abstract class AnnotatedConstantConfiguration
    extends Configuration
    implements AnnotatedConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> {
        @Override abstract AnnotatedConstantBindingBuilder bind(Binder binder);

        @Override public final ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> annotatedWith(
            final Class<? extends Annotation> annotationType) {
            return new ConstantConfiguration() {
                @Override void add(Configuration configuration) {
                    AnnotatedConstantConfiguration.this.add(configuration);
                }

                @Override ConstantBindingBuilder bind(Binder binder) {
                    return AnnotatedConstantConfiguration.this.bind(binder).annotatedWith(annotationType);
                }
            };
        }

        @Override public final ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> annotatedWith(
                final Annotation annotation) {
            return new ConstantConfiguration() {
                @Override void add(Configuration configuration) {
                    AnnotatedConstantConfiguration.this.add(configuration);
                }

                @Override ConstantBindingBuilder bind(Binder binder) {
                    return AnnotatedConstantConfiguration.this.bind(binder).annotatedWith(annotation);
                }
            };
        }
    }

    private abstract class ConstantConfiguration
    extends Configuration
    implements ConstantBindingBuilderWithInjection<ModuleBuilder<Parent>> {
        @Override abstract ConstantBindingBuilder bind(Binder binder);

        @Override public final Injection<ModuleBuilder<Parent>> to(final String value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final int value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final long value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final boolean value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final double value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final float value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final short value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final char value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final byte value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final Injection<ModuleBuilder<Parent>> to(final Class<?> value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }

        @Override public final <E extends Enum<E>> Injection<ModuleBuilder<Parent>> to(final E value) {
            return new Configuration() {
                @Override void add(Configuration configuration) {
                    ConstantConfiguration.this.add(configuration);
                }

                @Override Void bind(Binder binder) {
                    ConstantConfiguration.this.bind(binder).to(value);
                    return null;
                }
            };
        }
    }

    private abstract class Configuration
    implements Injection<ModuleBuilder<Parent>> {
        @Override public ModuleBuilder<Parent> inject() {
            add(this);
            return ModuleBuilder.this;
        }

        abstract void add(Configuration configuration);

        Object expose(PrivateBinder binder) { throw new AssertionError(); }
        Object bind(Binder binder) { throw new AssertionError(); }
    }
}
