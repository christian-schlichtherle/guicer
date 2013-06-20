package net.java.guicer;

import com.google.inject.Scope;
import java.lang.annotation.Annotation;

/**
 *
 * @author Christian Schlichtherle
 */
public interface ScopedBindingBuilderWithInjection<Parent>
extends Injection<Parent> {
    Injection<Parent> in(Class<? extends Annotation> scopeAnnotation);
    Injection<Parent> in(Scope scope);
    Injection<Parent> asEagerSingleton();
}
