package com.asistencia.asistencia_api.repository;

import com.asistencia.asistencia_api.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {}
