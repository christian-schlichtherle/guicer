/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.Injector;
import static com.google.inject.name.Names.named;
import javax.inject.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Christian Schlichtherle
 */
public class ModuleBuilderTest {

    @Test
    public void testConfiguration() {
        final Injector injector = new GuiceContext()
                .injector()
                    .module()
                        .bind(Bar.class)
                            .to(BarImpl.class)
                            .in(Singleton.class)
                            .inject()
                        .bind(Foo.class)
                            .annotatedWith(named("foo"))
                            .to(FooImpl.class)
                            .inject()
                        .inject()
                    .build();
        final Bar bar1 = injector.getInstance(Bar.class);
        final Bar bar2 = injector.getInstance(Bar.class);
        assertSame(bar1, bar2);
    }

    private interface Foo { }

    private static class FooImpl implements Foo { }

    private interface Bar { }

    //@Singleton
    private static class BarImpl implements Bar {
        @Inject BarImpl(@Named("foo") Foo foo) { }
    }
}
