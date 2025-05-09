package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.PlaceEntity;
import org.example.entity.request.PlaceRequest;
import org.example.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final RestTemplate restTemplate;
    @Value("${spring.url.place}")
    private String placesURL;

    public void fetchAndSavePlaces() {
        PlaceEntity[] places = restTemplate.getForObject(placesURL, PlaceEntity[].class);

        if (places != null) {
            log.info("Fetched places: {}", Arrays.stream(places).toList());
            placeRepository.saveAll(Arrays.asList(places));
        }
    }

    public List<PlaceEntity> getAllPlaces() {
        log.info("Getting all places: {}", placeRepository.findAll());
        return placeRepository.findAll();
    }

    public PlaceEntity getPlaceById(Long id) {
        log.info("Getting place by id: {}", placeRepository.findById(id));
        return placeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Place " + id));
    }

    public PlaceEntity createPlace(PlaceRequest request) {
        PlaceEntity place = new PlaceEntity();
        place.setSlug(request.getSlug());
        place.setName(request.getName());
        log.info("Saving place: {}", place.toString());

        return placeRepository.save(place);
    }

    public PlaceEntity updatePlace(Long id, PlaceRequest request) {
        PlaceEntity place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place " + id));
        log.info("Existing place: {}", place.toString());

        place.setSlug(request.getSlug());
        place.setName(request.getName());
        log.info("Updated place: {}", place.toString());

        return placeRepository.save(place);
    }

    public void deletePlace(Long id) {
        PlaceEntity place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place " + id));
        log.info("Place to delete: {}", place.toString());
        placeRepository.delete(place);
    }

}
