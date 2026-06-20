package precolloquio.progetto_meteo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cities")
@Setter @Getter
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome della città è obbligatorio")
    @Size(max = 150, message = "Il nome della città non può superare i 150 caratteri")
    private String name;

    @NotNull(message = "La latitudine è obbligatoria")
    @Min(value = -90, message = "La latitudine minima è -90")
    @Max(value = 90, message = "La latitudine massima è 90")
    private Double latitude;

    @NotNull(message = "La longitudine è obbligatoria")
    @Min(value = -180, message = "La longitudine minima è -180")
    @Max(value = 180, message = "La longitudine massima è 180")
    private Double longitude;

    public City(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}