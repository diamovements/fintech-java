package org.example;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CustomLinkedList<T> {

    private static class Node<T> {
        Node<T> next;
        Node<T> prev;
        T value;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> first;
    private Node<T> last;
    private int size = 0;

    public CustomLinkedList() {
        this.first = null;
    }

    public int size() {
        return size;
    }

    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (this.first == null) {
            first = node;
        } else {
            last.next = node;
            node.prev = last;
        }
        last = node;
        size++;
    }

    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        if (this.last == null) {
            last = node;
        } else {
            first.prev = node;
        }
        node.next = first;
        first = node;
        size++;
    }

    public void addAfter(T value, T key) {
        Node<T> current = first;
        while (current.value != key) {
            current = current.next;
            if (current == null) {
                return;
            }
        }
        Node<T> node = new Node<>(value);

        if (current == last) {
            node.next = null;
            last = node;
        } else {
            node.next = current.next;
            current.next.prev = node;
        }
        node.prev = current;
        current.next = node;
        size++;
    }

    public T remove(int index) {
        if (index < 0 || first == null) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        T value = get(index);
        Node<T> current = this.first;
        while (index > 0) {
            index--;
            current = current.next;
            if (current == null) {
                return null;
            }
        }
        if (current.prev != null) {
            current.prev.next = current.next;
        } else {
            this.first = current.next;
        }

        if (current.next != null) {
            current.next.prev = current.prev;
        } else {
            this.last = current.prev;
        }
        current.next = null;
        current.prev = null;
        size--;
        return value;
    }

    public T removeFirst() {
        if (first == null) {
            throw new IndexOutOfBoundsException("Index: 0, Size: " + size);
        }
        T value = first.value;
        if (first == last) {
            first = null;
            last = null;
        } else {
            first = first.next;
            first.prev = null;
        }
        size--;
        return value;
    }

    public T removeLast() {
        if (last == null) {
            throw new IndexOutOfBoundsException("Index: 0, Size: " + size);
        }
        T value = last.value;
        if (last == first) {
            first = null;
            last = null;
        } else {
            last = last.prev;
            last.next = null;
        }
        size--;
        return value;
    }

    public T get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        Node<T> current = this.first;
        while (index > 0) {
            index--;
            current = current.next;
            if (current == null) {
                return null;
            }
        }
        return current.value;
    }

    public T getFirst() {
        return first.value;
    }

    public T getLast() {
        return last.value;
    }

    public boolean contains(T value) {
        Node<T> current = this.first;
        while (current.next != null) {
            if (current.value == value) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean addAll(Collection<? extends T> collection) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        for (T item : collection) {
            addLast(item);
        }
        return true;
    }

    public static <T> CustomLinkedList<T> collect(Stream<T> stream) {
        return stream.reduce(new CustomLinkedList<>(),
                (list, element) -> {
                    list.addLast(element);
                    return list;
                },
                (list1, list2) -> {
                    list2.forEach(list1::addLast);
                    return list1;
                }
        );
    }

    public void forEach(Consumer<? super T> action) {
        Node<T> current = first;
        while (current != null) {
            action.accept(current.value);
            current = current.next;
        }
    }

    public CustomIterator<T> iterator() {
        return new CustomLinkedListIterator();
    }

    private class CustomLinkedListIterator implements CustomIterator<T> {
        private Node<T> current = first;
        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() throws NoSuchFieldException {
            if (!hasNext()) {
                throw new NoSuchFieldException("End of list");
            }
            T val = current.value;
            current = current.next;
            return val;
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) throws NoSuchFieldException {
            CustomIterator.super.forEachRemaining(action);
        }
    }
}

