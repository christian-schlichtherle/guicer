package net.java.guicer;

import java.lang.annotation.Annotation;

/**
 *
 * @author Christian Schlichtherle
 */
public interface AnnotatedConstantBindingBuilderWithInjection<Parent>
extends Injection<Parent> {
    ConstantBindingBuilderWithInjection<Parent> annotatedWith(
            Class<? extends Annotation> annotationType);
    ConstantBindingBuilderWithInjection<Parent> annotatedWith(Annotation annotation);
}
