package com.asistencia.asistencia_api.controller;

import com.asistencia.asistencia_api.dto.UsuarioRequest;
import com.asistencia.asistencia_api.model.Empleado;
import com.asistencia.asistencia_api.model.Usuario;
import com.asistencia.asistencia_api.repository.EmpleadoRepository;
import com.asistencia.asistencia_api.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;

    public UsuarioController(UsuarioRepository usuarioRepository,
                              EmpleadoRepository empleadoRepository) {
        this.usuarioRepository  = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Usuario> lista = usuarioRepository.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Usuario u : lista) {
            resultado.add(toMap(u));
        }
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody UsuarioRequest req) {
        Empleado emp = empleadoRepository.findById(req.getEmpleadoId()).orElse(null);
        if (emp == null) return ResponseEntity.badRequest().build();

        Usuario u = new Usuario();
        u.setUsuario(req.getUsuario());
        u.setContrasena(req.getContrasena());
        u.setRol(req.getRol());
        u.setEmpleado(emp);
        usuarioRepository.save(u);
        return ResponseEntity.ok(toMap(u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toMap(Usuario u) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",         u.getId());
        map.put("usuario",    u.getUsuario());
        map.put("correo",     u.getEmpleado() != null ? u.getEmpleado().getEmail() : "");
        map.put("nombre",     u.getEmpleado() != null ? u.getEmpleado().getNombre() : u.getUsuario());
        map.put("apellido",   u.getEmpleado() != null ? u.getEmpleado().getApellidos() : "");
        map.put("rol",        u.getRol());
        map.put("empleadoId", u.getEmpleado() != null ? u.getEmpleado().getId() : null);
        return map;
    }
}
