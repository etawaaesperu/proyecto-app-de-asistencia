package com.asistencia.asistencia_api.repository;

import com.asistencia.asistencia_api.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByFecha(String fecha);
}
