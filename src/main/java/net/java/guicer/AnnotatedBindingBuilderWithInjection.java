package net.java.guicer;

import java.lang.annotation.Annotation;

/**
 *
 * @author Christian Schlichtherle
 */
public interface AnnotatedBindingBuilderWithInjection<Type, Parent>
extends LinkedBindingBuilderWithInjection<Type, Parent> {
    LinkedBindingBuilderWithInjection<Type, Parent> annotatedWith(
            Class<? extends Annotation> annotationType);
    LinkedBindingBuilderWithInjection<Type, Parent> annotatedWith(
            Annotation annotation);
}
