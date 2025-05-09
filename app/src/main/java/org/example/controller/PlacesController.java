package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.PlaceEntity;
import org.example.entity.request.PlaceRequest;
import org.example.service.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places")
public class PlacesController {
    private final PlaceService placeService;

    @GetMapping()
    public List<PlaceEntity> getAllPlaces() {
        return placeService.getAllPlaces();
    }

    @GetMapping("/init")
    public void initPlaces() {
        placeService.fetchAndSavePlaces();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceEntity> getPlaceById(@PathVariable("id") Long id) {
        PlaceEntity place = placeService.getPlaceById(id);
        return place != null ? ResponseEntity.ok(place) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PlaceEntity> createPlace(@Valid @RequestBody PlaceRequest request) {
        PlaceEntity place = placeService.createPlace(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(place);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaceEntity> updatePlace(@PathVariable("id") Long id, @Valid @RequestBody PlaceRequest request) {
        PlaceEntity updatedPlace = placeService.updatePlace(id, request);
        return updatedPlace != null ? ResponseEntity.ok(updatedPlace) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PlaceEntity> deletePlace(@PathVariable("id") Long id) {
        PlaceEntity toDelete = placeService.getPlaceById(id);
        if (toDelete != null) {
            placeService.deletePlace(id);
        }
        return ResponseEntity.ok(toDelete);
    }

}
