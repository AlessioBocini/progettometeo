package precolloquio.progetto_meteo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import precolloquio.progetto_meteo.dto.CityAverageResponse;
import precolloquio.progetto_meteo.dto.MeteoResponse;
import precolloquio.progetto_meteo.entity.City;
import precolloquio.progetto_meteo.entity.WeatherMeasurement;
import precolloquio.progetto_meteo.repository.CityRepo;
import precolloquio.progetto_meteo.repository.WeatherMeasurementRepo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private CityRepo cityRepository;

    @Mock
    private WeatherMeasurementRepo measurementRepository;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    public void saveCity_WhenCityDoesNotExist_ShouldSaveAndReturnCity() {
        // Given
        City newCity = new City("Citta1", 45.4642, 9.1900);
        when(cityRepository.existsByNameIgnoreCase("Citta1")).thenReturn(false);
        when(cityRepository.save(newCity)).thenReturn(newCity);

        // When
        City savedCity = weatherService.saveCity(newCity);

        // Then
        assertThat(savedCity).isNotNull();
        assertThat(savedCity.getName()).isEqualTo("Citta1");
        verify(cityRepository, times(1)).save(newCity);
    }

    @Test
    public void saveCity_WhenCityAlreadyExists_ShouldThrowIllegalArgumentException() {
        // Given
        City duplicateCity = new City("Citta1", 45.4642, 9.1900);
        when(cityRepository.existsByNameIgnoreCase("Citta1")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> weatherService.saveCity(duplicateCity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La città Citta1 è già monitorata.");

        verify(cityRepository, never()).save(any(City.class));
    }

    @Test
    public void getAllCities_ShouldReturnListOfCities() {
        // Given
        City citta1 = new City("Citta1", 41.9028, 12.4964);
        City citta2 = new City("Citta2", 45.4642, 9.1900);
        when(cityRepository.findAll()).thenReturn(List.of(citta1, citta2));

        // When
        List<City> result = weatherService.getAllCities();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(citta1, citta2);
    }

    @Test
    public void deleteCity_ShouldCallRepositoryDelete() {
        // Given
        Long cityId = 1L;
        doNothing().when(cityRepository).deleteById(cityId);

        // When
        weatherService.deleteCity(cityId);

        // Then
        verify(cityRepository, times(1)).deleteById(cityId);
    }

    @Test
    public void getAllCityAverages_ShouldReturnCorrectlyRoundedAverages() {
        // Given
        City citta1 = new City("Citta1", 10.0, 20.0);
        citta1.setId(1L);
        Object[] dbRow = new Object[]{22.4444, 10.1111, 120.0};
        
        when(cityRepository.findAll()).thenReturn(List.of(citta1));
        doReturn(dbRow).when(measurementRepository).getAverageMetricsByCityId(1L);
        
        // When
        List<CityAverageResponse> result = weatherService.getAllCityAverages();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCityName()).isEqualTo("Citta1"); 
        assertThat(result.get(0).getAverageTemperature()).isEqualTo(22.44); 
        assertThat(result.get(0).getAverageWindSpeed()).isEqualTo(10.11);    
    }

    @Test
    public void getAllCityAverages_WhenNoMeasurementsExist_ShouldReturnZeroAverages() {
        // Given
        City citta1 = new City("Citta1", 10.0, 20.0);
        citta1.setId(1L);

        when(cityRepository.findAll()).thenReturn(List.of(citta1));
        doReturn(null).when(measurementRepository).getAverageMetricsByCityId(1L);

        // When
        List<CityAverageResponse> result = weatherService.getAllCityAverages();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAverageTemperature()).isEqualTo(0.0);
        assertThat(result.get(0).getAverageWindSpeed()).isEqualTo(0.0);
        assertThat(result.get(0).getAverageWindDirection()).isEqualTo(0.0);
    }

    @Test
    public void getAllCityAverages_WhenAveragesIsNull_ShouldReturnZeroAverages() {
        // Given
        City citta1 = new City("Citta1", 10.0, 20.0);
        citta1.setId(1L);

        when(cityRepository.findAll()).thenReturn(List.of(citta1));
        doReturn(null).when(measurementRepository).getAverageMetricsByCityId(1L);

        // When
        List<CityAverageResponse> result = weatherService.getAllCityAverages();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAverageTemperature()).isEqualTo(0.0);
        assertThat(result.get(0).getAverageWindSpeed()).isEqualTo(0.0);
        assertThat(result.get(0).getAverageWindDirection()).isEqualTo(0.0);
    }

    @Test
    public void getAllCityAverages_WhenAveragesIsEmptyArray_ShouldReturnZeroAverages() {
        // Given
        City citta1 = new City("Citta1", 10.0, 20.0);
        citta1.setId(1L);

        when(cityRepository.findAll()).thenReturn(List.of(citta1));
        doReturn(new Object[]{}).when(measurementRepository).getAverageMetricsByCityId(1L);

        // When
        List<CityAverageResponse> result = weatherService.getAllCityAverages();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAverageTemperature()).isEqualTo(0.0);
        assertThat(result.get(0).getAverageWindSpeed()).isEqualTo(0.0);
        assertThat(result.get(0).getAverageWindDirection()).isEqualTo(0.0);
    }

    @Test
    public void getAllCityAverages_WhenFirstElementIsNull_ShouldReturnZeroAverages() {
        // Given
        City citta1 = new City("Citta1", 10.0, 20.0);
        citta1.setId(1L);

        when(cityRepository.findAll()).thenReturn(List.of(citta1));
        doReturn(new Object[]{null, null, null}).when(measurementRepository).getAverageMetricsByCityId(1L);

        // When
        List<CityAverageResponse> result = weatherService.getAllCityAverages();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAverageTemperature()).isEqualTo(0.0);
        assertThat(result.get(0).getAverageWindSpeed()).isEqualTo(0.0);
        assertThat(result.get(0).getAverageWindDirection()).isEqualTo(0.0);
    }

    @Test
    public void fetchAndSaveWeatherData_ShouldFetchAndSaveCorrectly() {
        // Given
        City citta1 = new City("Citta1", 41.9028, 12.4964);
        citta1.setId(1L);

        MeteoResponse mockResponse = new MeteoResponse();
        MeteoResponse.CurrentWeather mockWeather = new MeteoResponse.CurrentWeather();
        mockWeather.setTemperature(15.5);
        mockWeather.setWindSpeed(12.3);
        mockWeather.setWindDirection(180.0);
        mockResponse.setCurrentWeather(mockWeather);

        try (MockedStatic<RestClient> mockedRestClientStatic = Mockito.mockStatic(RestClient.class)) {
            RestClient mockClient = mock(RestClient.class);
            RestClient.RequestHeadersUriSpec mockUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

            mockedRestClientStatic.when(RestClient::create).thenReturn(mockClient);
            when(mockClient.get()).thenReturn(mockUriSpec);
            when(mockUriSpec.uri(anyString())).thenReturn(mockUriSpec);
            when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.body(MeteoResponse.class)).thenReturn(mockResponse);

            WeatherService serviceWithMockedClient = new WeatherService(cityRepository, measurementRepository);

            // When
            serviceWithMockedClient.fetchAndSaveWeatherData(citta1);

            // Then
            verify(measurementRepository, times(1)).save(any(WeatherMeasurement.class));
        }
    }

    @Test
    public void fetchAndSaveWeatherData_WhenApiResponseIsNull_ShouldThrowIllegalStateException() {
        // Given
        City citta = new City("Citta1", 41.9028, 12.4964);

        try (MockedStatic<RestClient> mockedRestClientStatic = Mockito.mockStatic(RestClient.class)) {
            RestClient mockClient = mock(RestClient.class);
            RestClient.RequestHeadersUriSpec mockUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

            mockedRestClientStatic.when(RestClient::create).thenReturn(mockClient);
            when(mockClient.get()).thenReturn(mockUriSpec);
            when(mockUriSpec.uri(anyString())).thenReturn(mockUriSpec);
            when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.body(MeteoResponse.class)).thenReturn(null); 

            WeatherService serviceWithMockedClient = new WeatherService(cityRepository, measurementRepository);

            // When / Then
            assertThatThrownBy(() -> serviceWithMockedClient.fetchAndSaveWeatherData(citta))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Impossibile recuperare i dati meteo correnti dall'API esterna per Citta1");

            verify(measurementRepository, never()).save(any(WeatherMeasurement.class));
        }
    }

    @Test
    public void fetchAndSaveWeatherData_WhenCurrentWeatherIsNull_ShouldThrowIllegalStateException() {
        // Given
        City citta = new City("Citta1", 41.9028, 12.4964);
        MeteoResponse mockResponseWithNoWeather = new MeteoResponse();
        mockResponseWithNoWeather.setCurrentWeather(null); 

        try (MockedStatic<RestClient> mockedRestClientStatic = Mockito.mockStatic(RestClient.class)) {
            RestClient mockClient = mock(RestClient.class);
            RestClient.RequestHeadersUriSpec mockUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

            mockedRestClientStatic.when(RestClient::create).thenReturn(mockClient);
            when(mockClient.get()).thenReturn(mockUriSpec);
            when(mockUriSpec.uri(anyString())).thenReturn(mockUriSpec);
            when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.body(MeteoResponse.class)).thenReturn(mockResponseWithNoWeather); 

            WeatherService serviceWithMockedClient = new WeatherService(cityRepository, measurementRepository);

            // When / Then
            assertThatThrownBy(() -> serviceWithMockedClient.fetchAndSaveWeatherData(citta))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Impossibile recuperare i dati meteo correnti dall'API esterna per Citta1");

            verify(measurementRepository, never()).save(any(WeatherMeasurement.class));
        }
    }
}