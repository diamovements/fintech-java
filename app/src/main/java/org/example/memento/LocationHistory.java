package org.example.memento;

import java.util.ArrayList;
import java.util.List;

public class LocationHistory {
    private final List<LocationSnapshot> history = new ArrayList<>();
    public void save(LocationSnapshot snapshot) {
        history.add(snapshot);
    }
    public LocationSnapshot lastSnapshot() {
        if (history.isEmpty()) throw new IllegalStateException("History is empty");
        return history.get(history.size() - 1);
    }

    public List<LocationSnapshot> getHistory() {
        return new ArrayList<>(history);
    }
}
