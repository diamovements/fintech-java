package org.example.memento;

public interface Snapshot<T>{
    T restore();
}
