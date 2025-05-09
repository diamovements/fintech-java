package org.example;

import java.util.function.Consumer;

public interface CustomIterator<E> {
    boolean hasNext();
    E next() throws NoSuchFieldException;
    default void forEachRemaining(Consumer<? super E> action) throws NoSuchFieldException {
        while (hasNext()) {
            action.accept(next());
        }
    }
}
