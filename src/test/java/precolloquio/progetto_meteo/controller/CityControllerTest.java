package precolloquio.progetto_meteo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import precolloquio.progetto_meteo.entity.City;
import precolloquio.progetto_meteo.service.WeatherService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private CityController cityController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        // Inizializza MockMvc standalone puntando direttamente al controller con il suo mock iniettato
        this.mockMvc = MockMvcBuilders.standaloneSetup(cityController).build();
    }

    @Test
    public void addCity_WhenValidCity_ShouldReturnCreated() throws Exception {
        // Given
        City inputCity = new City("Citta1", 45.4642, 9.1900);
        City savedCity = new City("Citta1", 45.4642, 9.1900);
        savedCity.setId(1L);

        when(weatherService.saveCity(any(City.class))).thenReturn(savedCity);

        // When / Then
        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Citta1"));

        verify(weatherService, times(1)).saveCity(any(City.class));
    }

    @Test
    public void getAllCities_ShouldReturnListOfCities() throws Exception {
        // Given
        City city1 = new City("Citta1", 45.4642, 9.1900);
        city1.setId(1L);
        when(weatherService.getAllCities()).thenReturn(List.of(city1));

        // When / Then
        mockMvc.perform(get("/api/v1/cities")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Citta1"));

        verify(weatherService, times(1)).getAllCities();
    }

    @Test
    public void deleteCity_ShouldReturnNoContent() throws Exception {
        // Given
        Long cityId = 1L;
        doNothing().when(weatherService).deleteCity(cityId);

        // When / Then
        mockMvc.perform(delete("/api/v1/cities/{id}", cityId))
                .andExpect(status().isNoContent());

        verify(weatherService, times(1)).deleteCity(cityId);
    }
}