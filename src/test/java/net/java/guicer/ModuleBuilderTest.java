/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

import com.google.inject.Module;
import com.google.inject.PrivateModule;
import static com.google.inject.name.Names.named;
import javax.inject.Singleton;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Christian Schlichtherle
 */
public class ModuleBuilderTest {

    @Ignore("Module.equals() is not defined.")
    @Test
    public void testConfiguration() {
        final Module guice = new PrivateModule() {
            @Override protected void configure() {
                expose(Object.class)
                        .annotatedWith(named("object"));
                bind(Object.class)
                        .annotatedWith(named("object"))
                        .to(Object.class)
                        .in(Singleton.class);
            }
        };
        final Module guicer = new GuiceContext()
                .injector()
                    .module()
                        .exposeAndBind(Object.class)
                            .to(Object.class)
                            .annotatedWith(named("object"))
                            .in(Singleton.class)
                            .inject()
                        .build();
        assertEquals(guice, guicer);
    }
}
