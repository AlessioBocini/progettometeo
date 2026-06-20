package precolloquio.progetto_meteo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CityAverageResponse {
    private String cityName;
    private Double averageTemperature;
    private String temperatureUnit = "°C";
    private Double averageWindSpeed;
    private String windSpeedUnit = "km/h";
    private Double averageWindDirection;
    private String windDirectionUnit = "°";

    public CityAverageResponse(String cityName, Double avgTemp, Double avgWindSpeed, Double avgWindDir) {
        this.cityName = cityName;
        this.averageTemperature = format(avgTemp);
        this.averageWindSpeed = format(avgWindSpeed);
        this.averageWindDirection = format(avgWindDir);
    }

    // Arrotonda a 2 decimali per un JSON pulito
    private Double format(Double value) {
        if (value == null) return 0.0;
        return Math.round(value * 100.0) / 100.0;
    }

}