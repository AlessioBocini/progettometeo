package precolloquio.progetto_meteo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import precolloquio.progetto_meteo.entity.City;
import precolloquio.progetto_meteo.entity.WeatherMeasurement;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class WeatherMeasurementRepoTest {

    @Autowired
    private WeatherMeasurementRepo measurementRepo;

    @Autowired
    private CityRepo cityRepo;

    @Test
    public void findByCityIdOrderByTimestampDesc_ShouldReturnOrderedMeasurements() {
        // Given
        City citta2 = cityRepo.save(new City("citta2", 45.4642, 9.1900));
        
        WeatherMeasurement m1 = new WeatherMeasurement(citta2, 20.0, 10.0, 180.0, LocalDateTime.now().minusHours(2));
        WeatherMeasurement m2 = new WeatherMeasurement(citta2, 25.0, 15.0, 190.0, LocalDateTime.now().minusHours(1)); // Più recente
        
        measurementRepo.saveAll(List.of(m1, m2));

        // When
        List<WeatherMeasurement> results = measurementRepo.findByCityIdOrderByTimestampDesc(citta2.getId());

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTemperature()).isEqualTo(25.0); // Il più recente deve essere il primo
        assertThat(results.get(1).getTemperature()).isEqualTo(20.0);
    }

    @Test
    public void getAverageMetricsByCityId_ShouldCalculateCorrectAverages() {
        // Given
        City citta2 = cityRepo.save(new City("citta2", 45.4642, 9.1900));
        
        WeatherMeasurement m1 = new WeatherMeasurement(citta2, 10.0, 5.0, 100.0, LocalDateTime.now());
        WeatherMeasurement m2 = new WeatherMeasurement(citta2, 20.0, 15.0, 200.0, LocalDateTime.now());
        measurementRepo.saveAll(List.of(m1, m2));

        // When
        Object[] results = measurementRepo.getAverageMetricsByCityId(citta2.getId());

        // Then
        assertThat(results).isNotNull();

        Object[] averages = (Object[]) results[0];
        assertThat(averages.length).isEqualTo(3);
        assertThat((Double) averages[0]).isEqualTo(15.0);   
        assertThat((Double) averages[1]).isEqualTo(10.0);   
        assertThat((Double) averages[2]).isEqualTo(150.0);  
    }

    @Test
    public void getAverageMetricsByCityId_WhenNoMeasurements_ShouldReturnNullElements() {
        // Given
        City citta1 = cityRepo.save(new City("Citta1", 41.9028, 12.4964));

        // When
        Object[] results = measurementRepo.getAverageMetricsByCityId(citta1.getId());

        // Then
        assertThat(results).isNotNull();
        
        Object[] averages = (Object[]) results[0];
      
        assertThat(averages).isNotNull();
        assertThat(averages[0]).isNull();
        assertThat(averages[1]).isNull();
        assertThat(averages[2]).isNull();
    }
}