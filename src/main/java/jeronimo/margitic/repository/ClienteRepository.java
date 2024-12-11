package jeronimo.margitic.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jeronimo.margitic.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer>{

    Optional<Cliente> findById(int id);
    
    Optional<Cliente> findByDni(long dni);

}
