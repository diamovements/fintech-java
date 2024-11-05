package org.example.memento;

import lombok.Getter;
import org.example.entity.Location;
import java.time.LocalDateTime;

@Getter
public class LocationSnapshot implements Snapshot<Location>{
    private final String slug;
    private final String name;
    private final LocalDateTime timestamp;

    public LocationSnapshot(Location location) {
        this.slug = location.getSlug();
        this.name = location.getName();
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public Location restore() {
        Location location = new Location();
        location.setSlug(this.slug);
        location.setName(this.name);
        return location;
    }
}
