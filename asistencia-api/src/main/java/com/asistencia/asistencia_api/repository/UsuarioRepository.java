package com.asistencia.asistencia_api.repository;

import com.asistencia.asistencia_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuarioAndContrasena(String usuario, String contrasena);
}
