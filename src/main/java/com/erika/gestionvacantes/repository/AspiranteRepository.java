package com.erika.gestionvacantes.repository;

import com.erika.gestionvacantes.model.Aspirante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AspiranteRepository extends JpaRepository<Aspirante, Long> {
    Optional<Aspirante> findByCorreo(String correo);
}
