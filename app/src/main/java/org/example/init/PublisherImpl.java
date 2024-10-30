package org.example.init;

import java.util.ArrayList;
import java.util.List;

public class PublisherImpl<T> implements Publisher<T> {
    private final List<Subscriber<T>> subscribers = new ArrayList<>();
    @Override
    public void subscribe(Subscriber<T> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber<T> subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void publish(T t) {
        subscribers.forEach(subscriber -> subscriber.update(t));
    }
}
