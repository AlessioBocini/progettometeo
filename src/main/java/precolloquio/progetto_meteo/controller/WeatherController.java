package precolloquio.progetto_meteo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import precolloquio.progetto_meteo.dto.CityAverageResponse;
import precolloquio.progetto_meteo.entity.City;
import precolloquio.progetto_meteo.service.WeatherService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<CityAverageResponse>> getMetrics() {
        List<CityAverageResponse> averages = weatherService.getAllCityAverages();
        return ResponseEntity.ok(averages);
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> syncWeatherData() {
        List<City> cities = weatherService.getAllCities();
        
        if (cities.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nessuna città presente nel database. Aggiungi prima una città."));
        }

        // Cicla sulle città e chiama Open-Meteo, propagando eventuali eccezioni
        for (City city : cities) {
            weatherService.fetchAndSaveWeatherData(city);
        }

        return ResponseEntity.ok(Map.of("message", "Sincronizzazione completata con successo per " + cities.size() + " città."));
    }
}