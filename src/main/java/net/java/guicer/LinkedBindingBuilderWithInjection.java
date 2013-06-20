package net.java.guicer;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import java.lang.reflect.Constructor;

/**
 *
 * @author Christian Schlichtherle
 */
public interface LinkedBindingBuilderWithInjection<Type, Parent>
        extends ScopedBindingBuilderWithInjection<Parent> {
    ScopedBindingBuilderWithInjection<Parent> to(
            Class<? extends Type> implementation);
    ScopedBindingBuilderWithInjection<Parent> to(
            TypeLiteral<? extends Type> implementation);
    ScopedBindingBuilderWithInjection<Parent> to(
            Key<? extends Type> targetKey);
    Injection<Parent> toInstance(Type instance);
    ScopedBindingBuilderWithInjection<Parent> toProvider(
            Provider<? extends Type> provider);
    ScopedBindingBuilderWithInjection<Parent> toProvider(
            Class<? extends javax.inject.Provider<? extends Type>> providerType);
    ScopedBindingBuilderWithInjection<Parent> toProvider(
            TypeLiteral<? extends javax.inject.Provider<? extends Type>> providerType);
    ScopedBindingBuilderWithInjection<Parent> toProvider(
            Key<? extends javax.inject.Provider<? extends Type>> providerKey);
    <S extends Type> ScopedBindingBuilderWithInjection<Parent> toConstructor(
            Constructor<S> constructor);
    <S extends Type> ScopedBindingBuilderWithInjection<Parent> toConstructor(
            Constructor<S> constructor, TypeLiteral<? extends S> type);
}
