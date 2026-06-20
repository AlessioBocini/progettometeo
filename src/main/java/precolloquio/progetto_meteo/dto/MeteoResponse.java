package precolloquio.progetto_meteo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MeteoResponse {

    @JsonProperty("current_weather")
    private CurrentWeather currentWeather;

    @Getter @Setter
    public static class CurrentWeather {
        private Double temperature;
        @JsonProperty("windspeed")
        private Double windSpeed;
        @JsonProperty("winddirection")
        private Double windDirection;
    }
}