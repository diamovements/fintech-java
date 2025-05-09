package org.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CustomLinkedListTest {
    CustomLinkedList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    void testSize() {
        assertEquals(0, list.size());
        list.addLast(10);
        assertEquals(1, list.size());
    }

    @Test
    void testAddLast() {
        list.addLast(10);
        assertEquals(1, list.size());
        assertEquals(10, list.getLast());
    }

    @Test
    void testAddFirst() {
        list.addFirst(20);
        assertEquals(1, list.size());
        assertEquals(20, list.getFirst());
        list.addFirst(10);
        assertEquals(10, list.getFirst());
    }

    @Test
    void testAddAfter() {
        list.addLast(10);
        list.addLast(20);
        list.addAfter(15, 10);
        assertEquals(15, list.get(1));
    }

    @Test
    void testRemoveByIndex() {
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);
        assertEquals(20, list.remove(1));
        assertEquals(2, list.size());
    }
    @Test
    void testRemoveFirst() {
        list.addLast(10);
        list.addLast(20);
        assertEquals(10, list.removeFirst());
        assertEquals(1, list.size());
    }

    @Test
    void testRemoveLast() {
        list.addLast(10);
        list.addLast(20);
        assertEquals(20, list.removeLast());
        assertEquals(1, list.size());
    }

    @Test
    void testGet() {
        list.addLast(10);
        list.addLast(20);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
    }

    @Test
    void testGetFirst() {
        list.addLast(10);
        list.addLast(20);
        assertEquals(10, list.getFirst());
    }

    @Test
    void testGetLast() {
        list.addLast(10);
        list.addLast(20);
        assertEquals(20, list.getLast());
    }

    @Test
    void testContains() {
        list.addLast(10);
        list.addLast(20);
        assertTrue(list.contains(10));
        assertFalse(list.contains(30));
    }

    @Test
    void testAddAll() {
        List<Integer> items = new ArrayList<>();
        list.addLast(40);
        items.add(10);
        items.add(20);
        items.add(30);
        assertTrue(list.addAll(items));
        assertEquals(4, list.size());
        assertEquals(40, list.getFirst());
        assertEquals(30, list.getLast());
    }

    //2 часть ДЗ
    @Test
    void testCollect() {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4);
        list = CustomLinkedList.collect(stream);
        assertEquals(4, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(4, list.get(3));
    }

    @Test
    void testForEach() {
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);
        List<Integer> result = new ArrayList<>();
        list.forEach(result::add);
        assertEquals(List.of(10, 20, 30), result);
    }

    @Test
    void testRemoveWithNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    }

    @Test
    void testGetWithNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }
}
