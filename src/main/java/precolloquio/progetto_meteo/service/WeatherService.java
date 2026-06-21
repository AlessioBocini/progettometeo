package precolloquio.progetto_meteo.service;

import precolloquio.progetto_meteo.dto.CityAverageResponse;
import precolloquio.progetto_meteo.dto.MeteoResponse;
import precolloquio.progetto_meteo.entity.City;
import precolloquio.progetto_meteo.entity.WeatherMeasurement;
import precolloquio.progetto_meteo.repository.CityRepo;
import precolloquio.progetto_meteo.repository.WeatherMeasurementRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;



import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service 
public class WeatherService {

    private final CityRepo cityRepository;
    private final WeatherMeasurementRepo measurementRepository;
    private final RestClient restClient;

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    public WeatherService(CityRepo cityRepository, WeatherMeasurementRepo measurementRepository) {
        this.cityRepository = cityRepository;
        this.measurementRepository = measurementRepository;
        this.restClient = RestClient.create();
    }

    public City saveCity(City city) {
        if (cityRepository.existsByNameIgnoreCase(city.getName())) {
            throw new IllegalArgumentException("La città " + city.getName() + " è già monitorata.");
        }
        return cityRepository.save(city);
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
    }

    public void fetchAndSaveWeatherData(City city) {
        String url = String.format(
            "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true",
            city.getLatitude(), city.getLongitude()
        );

        
        MeteoResponse response = restClient.get()
                .uri(url).retrieve()
                .body(MeteoResponse.class);
        
        if (response == null || response.getCurrentWeather() == null) {
            throw new IllegalStateException("Impossibile recuperare i dati meteo correnti dall'API esterna per " + city.getName());
        }

        MeteoResponse.CurrentWeather current = response.getCurrentWeather();
    
        WeatherMeasurement measurement = new WeatherMeasurement(
                city,
                current.getTemperature(),
                current.getWindSpeed(),
                current.getWindDirection(),
                LocalDateTime.now()
        );
        
        measurementRepository.save(measurement);
    }

    public List<CityAverageResponse> getAllCityAverages() {
        List<City> cities = cityRepository.findAll();
        
        return cities.stream().map(city -> {
            List<Object[]> results = measurementRepository.getAverageMetricsByCityId(city.getId());
            
            Double avgTemp = 0.0;
            Double avgWindSpeed = 0.0;
            Double avgWindDir = 0.0;

            if (results != null && !results.isEmpty()) {
                Object[] averages = results.get(0); 
                
                if (averages != null && averages.length > 0 && averages[0] != null) {
                
                    avgTemp = averages[0] != null ? ((Number) averages[0]).doubleValue() : 0.0;
                    avgWindSpeed = averages[1] != null ? ((Number) averages[1]).doubleValue() : 0.0;
                    avgWindDir = averages[2] != null ? ((Number) averages[2]).doubleValue() : 0.0;
                }
            }

            return new CityAverageResponse(city.getName(), avgTemp, avgWindSpeed, avgWindDir);
        }).collect(Collectors.toList());
    }



    @Scheduled(fixedRate = 10000) 
    public void syncWeatherData() {
        logger.info("Inizio sincronizzazione pianificata dei dati meteo...");
        
        List<City> cities = cityRepository.findAll();
        
        for (City city : cities) {
            try {
                logger.info("Recupero dati meteo per: {}", city.getName());
                fetchAndSaveWeatherData(city);
            } catch (Exception e) {
                logger.error("Errore durante il recupero dei dati per " + city.getName(), e);
            }
        }
        
        logger.info("Sincronizzazione completata.");
    }
}