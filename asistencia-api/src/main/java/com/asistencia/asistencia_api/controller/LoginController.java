package com.asistencia.asistencia_api.controller;

import com.asistencia.asistencia_api.dto.LoginRequest;
import com.asistencia.asistencia_api.dto.LoginResponse;
import com.asistencia.asistencia_api.dto.UsuarioData;
import com.asistencia.asistencia_api.model.Usuario;
import com.asistencia.asistencia_api.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Optional<Usuario> opt = usuarioRepository
                .findByUsuarioAndContrasena(request.getUsuario(), request.getContrasena());

        if (opt.isEmpty()) {
            return ResponseEntity.ok(
                    new LoginResponse(false, "Usuario o contraseña incorrectos", null)
            );
        }

        Usuario u = opt.get();
        Long idEmpleado = (u.getEmpleado() != null) ? u.getEmpleado().getId() : 0L;
        String nombre   = (u.getEmpleado() != null) ? u.getEmpleado().getNombre()   : u.getUsuario();
        String apellido = (u.getEmpleado() != null) ? u.getEmpleado().getApellidos() : "";

        UsuarioData data = new UsuarioData(u.getId(), nombre, apellido, u.getRol(), idEmpleado);
        return ResponseEntity.ok(new LoginResponse(true, "Login exitoso", data));
    }
}
