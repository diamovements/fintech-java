package org.example;

import java.util.List;

public class App {
    public static void main(String[] args) {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(11);
        System.out.println(list.contains(11));
        System.out.println(list.get(0));
        System.out.println(list.size());
        List<Integer> new_list = List.of(1, 2, 3);
        list.addAll(new_list);
        list.remove(0);
    }
}
