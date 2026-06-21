package precolloquio.progetto_meteo.repository;

import precolloquio.progetto_meteo.entity.WeatherMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WeatherMeasurementRepo extends JpaRepository<WeatherMeasurement, Long> {

    List<WeatherMeasurement> findByCityIdOrderByTimestampDesc(Long cityId);

    @Query("""
       SELECT AVG(wm.temperature), AVG(wm.windSpeed), AVG(wm.windDirection) 
       FROM WeatherMeasurement wm 
       WHERE wm.city.id = :cityId
    """)
    List<Object[]> getAverageMetricsByCityId(@Param("cityId") Long cityId);
}