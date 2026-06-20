package precolloquio.progetto_meteo.repository;

import precolloquio.progetto_meteo.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepo extends JpaRepository<City, Long> {
    boolean existsByNameIgnoreCase(String name);
}