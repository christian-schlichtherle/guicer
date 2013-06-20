package net.java.guicer;

import java.lang.annotation.Annotation;

/**
 *
 * @author Christian Schlichtherle
 */
public interface AnnotatedElementBuilderWithInjection<Parent>
extends Injection<Parent> {
    Injection<Parent> annotatedWith(Class<? extends Annotation> annotationType);
    Injection<Parent> annotatedWith(Annotation annotation);
}
