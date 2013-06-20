package net.java.guicer;

/**
 *
 * @author Christian Schlichtherle
 */
public interface ConstantBindingBuilderWithInjection<Parent>
extends Injection<Parent> {
    Injection<Parent> to(String value);
    Injection<Parent> to(int value);
    Injection<Parent> to(long value);
    Injection<Parent> to(boolean value);
    Injection<Parent> to(double value);
    Injection<Parent> to(float value);
    Injection<Parent> to(short value);
    Injection<Parent> to(char value);
    Injection<Parent> to(byte value);
    Injection<Parent> to(Class<?> value);
    <E extends Enum<E>> Injection<Parent> to(E value);
}
