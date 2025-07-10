package jeronimo.margitic.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import jeronimo.margitic.model.Obra;
import java.util.Optional;


@Repository
public interface ObraRepository extends JpaRepository<Obra, Integer>{

    Optional<Obra> findById(int id);
    
}
