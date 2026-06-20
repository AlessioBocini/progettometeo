package precolloquio.progetto_meteo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import precolloquio.progetto_meteo.dto.CityAverageResponse;
import precolloquio.progetto_meteo.entity.City;
import precolloquio.progetto_meteo.service.WeatherService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    @Test
    public void getMetrics_ShouldReturnAveragesList() throws Exception {
        // Given
        CityAverageResponse responseDto = new CityAverageResponse("Citta1", 15.5, 12.2, 180.0);
        when(weatherService.getAllCityAverages()).thenReturn(List.of(responseDto));

        // When / Then
        mockMvc.perform(get("/api/v1/weather/metrics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].cityName").value("Citta1"));

        verify(weatherService, times(1)).getAllCityAverages();
    }

    @Test
    public void syncWeatherData_WhenNoCitiesExist_ShouldReturnBadRequest() throws Exception {
        // Given
        when(weatherService.getAllCities()).thenReturn(Collections.emptyList());

        // When / Then
        mockMvc.perform(post("/api/v1/weather/sync"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nessuna città presente nel database. Aggiungi prima una città."));

        verify(weatherService, never()).fetchAndSaveWeatherData(any(City.class));
    }

    @Test
    public void syncWeatherData_WhenCitiesExist_ShouldSyncAndReturnOk() throws Exception {
        // Given
        City city1 = new City("Citta1", 45.4642, 9.1900);
        when(weatherService.getAllCities()).thenReturn(List.of(city1));
        doNothing().when(weatherService).fetchAndSaveWeatherData(any(City.class));

        // When / Then
        mockMvc.perform(post("/api/v1/weather/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sincronizzazione completata con successo per 1 città."));

        verify(weatherService, times(1)).fetchAndSaveWeatherData(city1);
    }
}