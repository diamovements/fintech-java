package org.example.config;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class History<T> {
    private final List<T> history = new ArrayList<>();
    public void save(T snapshot) {
        history.add(snapshot);
    }
    public Optional<T> lastSnapshot() {
        return history.isEmpty() ? Optional.empty() : Optional.of(history.get(history.size() - 1));
    }

    public List<T> getHistory() {
        return new ArrayList<>(history);
    }
}
