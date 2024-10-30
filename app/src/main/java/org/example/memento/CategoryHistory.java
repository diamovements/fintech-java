package org.example.memento;

import java.util.ArrayList;
import java.util.List;

public class CategoryHistory {
    private final List<CategorySnapshot> history = new ArrayList<>();
    public void save(CategorySnapshot snapshot) {
        history.add(snapshot);
    }
    public CategorySnapshot lastSnapshot() {
        if (history.isEmpty()) throw new IllegalStateException("History is empty");
        return history.get(history.size() - 1);
    }

    public List<CategorySnapshot> getHistory() {
        return new ArrayList<>(history);
    }
}
