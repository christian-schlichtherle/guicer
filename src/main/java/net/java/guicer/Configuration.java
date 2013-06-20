/*
 * Copyright (C) 2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.guicer;

/**
 * Configures a target.
 * This is not part of the DSL, so it's package private.
 *
 * @param  <Target> The type of the installation target for this configuration
 *         item.
 * @author Christian Schlichtherle
 */
interface Configuration<Target> {
    void configure(Target target);
}
