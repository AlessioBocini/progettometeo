package precolloquio.progetto_meteo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import precolloquio.progetto_meteo.entity.City;
import precolloquio.progetto_meteo.service.WeatherService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final WeatherService weatherService;

    public CityController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping
    public ResponseEntity<City> addCity(@Valid @RequestBody City city) {
        City savedCity = weatherService.saveCity(city);
        return new ResponseEntity<>(savedCity, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        return ResponseEntity.ok(weatherService.getAllCities());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        weatherService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}