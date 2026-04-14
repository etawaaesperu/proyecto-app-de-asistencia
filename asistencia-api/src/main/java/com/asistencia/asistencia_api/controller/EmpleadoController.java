package com.asistencia.asistencia_api.controller;

import com.asistencia.asistencia_api.dto.EmpleadoRequest;
import com.asistencia.asistencia_api.model.Empleado;
import com.asistencia.asistencia_api.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoRepository    empleadoRepository;
    private final UsuarioRepository     usuarioRepository;
    private final HorarioRepository     horarioRepository;
    private final AsistenciaRepository  asistenciaRepository;

    public EmpleadoController(EmpleadoRepository empleadoRepository,
                               UsuarioRepository usuarioRepository,
                               HorarioRepository horarioRepository,
                               AsistenciaRepository asistenciaRepository) {
        this.empleadoRepository   = empleadoRepository;
        this.usuarioRepository    = usuarioRepository;
        this.horarioRepository    = horarioRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Empleado> empleados = empleadoRepository.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Empleado e : empleados) {
            boolean tieneUsuario = usuarioRepository.findAll()
                    .stream()
                    .anyMatch(u -> u.getEmpleado() != null && u.getEmpleado().getId().equals(e.getId()));
            resultado.add(toMap(e, tieneUsuario));
        }
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody EmpleadoRequest req) {
        Empleado e = new Empleado();
        mapearCampos(e, req);
        e = empleadoRepository.save(e);
        return ResponseEntity.ok(toMap(e, false));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editar(@PathVariable Long id,
                                                       @RequestBody EmpleadoRequest req) {
        return empleadoRepository.findById(id).map(e -> {
            mapearCampos(e, req);
            empleadoRepository.save(e);
            boolean tieneUsuario = usuarioRepository.findAll()
                    .stream()
                    .anyMatch(u -> u.getEmpleado() != null && u.getEmpleado().getId().equals(e.getId()));
            return ResponseEntity.ok(toMap(e, tieneUsuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    // HU14 — eliminar en cascada: asistencias → horarios → usuario → empleado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!empleadoRepository.existsById(id)) return ResponseEntity.notFound().build();

        // 1. Eliminar asistencias
        asistenciaRepository.findAll().stream()
                .filter(a -> a.getEmpleado() != null && a.getEmpleado().getId().equals(id))
                .forEach(a -> asistenciaRepository.deleteById(a.getId()));

        // 2. Eliminar horarios
        horarioRepository.findAll().stream()
                .filter(h -> h.getEmpleado() != null && h.getEmpleado().getId().equals(id))
                .forEach(h -> horarioRepository.deleteById(h.getId()));

        // 3. Eliminar usuario asociado
        usuarioRepository.findAll().stream()
                .filter(u -> u.getEmpleado() != null && u.getEmpleado().getId().equals(id))
                .forEach(u -> usuarioRepository.deleteById(u.getId()));

        // 4. Eliminar empleado
        empleadoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private void mapearCampos(Empleado e, EmpleadoRequest req) {
        e.setNombre(req.getNombre());
        e.setApellidos(req.getApellidos());
        e.setDni(req.getDni());
        e.setEmail(req.getEmail());
        e.setTelefono(req.getTelefono());
        e.setFechaNacimiento(req.getFechaNacimiento());
        e.setEstadoCivil(req.getEstadoCivil());
        e.setCargo(req.getCargo());
        e.setDepartamento(req.getDepartamento());
    }

    private Map<String, Object> toMap(Empleado e, boolean tieneUsuario) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",              e.getId());
        map.put("codigo",          e.getCodigo() != null ? e.getCodigo() : "");
        map.put("nombre",          e.getNombre());
        map.put("apellidos",       e.getApellidos());
        map.put("departamento",    e.getDepartamento() != null ? e.getDepartamento() : "");
        map.put("cargo",           e.getCargo() != null ? e.getCargo() : "");
        map.put("dni",             e.getDni());
        map.put("email",           e.getEmail());
        map.put("telefono",        e.getTelefono());
        map.put("fechaNacimiento", e.getFechaNacimiento());
        map.put("estadoCivil",     e.getEstadoCivil());
        map.put("tieneUsuario",    tieneUsuario);
        return map;
    }
}
