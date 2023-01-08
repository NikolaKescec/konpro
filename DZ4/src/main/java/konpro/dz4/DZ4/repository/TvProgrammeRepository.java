package konpro.dz4.DZ4.repository;

import konpro.dz4.DZ4.entity.TvProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TvProgrammeRepository extends JpaRepository<TvProgramme, Long> {

}
