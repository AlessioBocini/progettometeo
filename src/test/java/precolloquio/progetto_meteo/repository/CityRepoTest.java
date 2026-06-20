package precolloquio.progetto_meteo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import precolloquio.progetto_meteo.entity.City;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CityRepoTest {

    @Autowired
    private CityRepo cityRepo;

    @Test
    public void existsByNameIgnoreCase_WhenCityExistsWithDifferentCase_ShouldReturnTrue() {
        // Given
        City city = new City("Citta1", 45.4642, 9.1900);
        cityRepo.save(city);

        // When
        boolean exists = cityRepo.existsByNameIgnoreCase("citta1");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    public void existsByNameIgnoreCase_WhenCityDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = cityRepo.existsByNameIgnoreCase("citta2");

        // Then
        assertThat(exists).isFalse();
    }
}