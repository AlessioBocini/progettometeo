package precolloquio.progetto_meteo.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CityAverageResponseTest {

    @Test
    public void constructor_WhenValuesAreNull_ShouldReturnZeroAverages() {
        // Given / When
        CityAverageResponse response = new CityAverageResponse("Torino", null, null, null);

        // Then
        assertThat(response.getAverageTemperature()).isEqualTo(0.0);
        assertThat(response.getAverageWindSpeed()).isEqualTo(0.0);
        assertThat(response.getAverageWindDirection()).isEqualTo(0.0);
    }

    @Test
    public void constructor_WhenValuesAreValid_ShouldRoundToTwoDecimals() {
        // Given / When
        CityAverageResponse response = new CityAverageResponse("Roma", 22.4444, 10.1111, 120.005);

        // Then
        assertThat(response.getAverageTemperature()).isEqualTo(22.44);
        assertThat(response.getAverageWindSpeed()).isEqualTo(10.11);
        assertThat(response.getAverageWindDirection()).isEqualTo(120.01);
    }
}