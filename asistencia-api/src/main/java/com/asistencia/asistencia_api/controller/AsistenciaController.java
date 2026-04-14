package com.asistencia.asistencia_api.controller;

import com.asistencia.asistencia_api.dto.AsistenciaRequest;
import com.asistencia.asistencia_api.model.Asistencia;
import com.asistencia.asistencia_api.model.Empleado;
import com.asistencia.asistencia_api.repository.AsistenciaRepository;
import com.asistencia.asistencia_api.repository.EmpleadoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    private final AsistenciaRepository asistenciaRepository;
    private final EmpleadoRepository   empleadoRepository;

    public AsistenciaController(AsistenciaRepository asistenciaRepository,
                                 EmpleadoRepository empleadoRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.empleadoRepository   = empleadoRepository;
    }

    // GET /api/asistencia?fecha=yyyy-MM-dd
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar(
            @RequestParam(required = false) String fecha) {

        List<Asistencia> lista = (fecha != null && !fecha.isEmpty())
                ? asistenciaRepository.findByFecha(fecha)
                : asistenciaRepository.findAll();

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Asistencia a : lista) resultado.add(toMap(a));
        return ResponseEntity.ok(resultado);
    }

    // POST /api/asistencia  → registrar entrada
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody AsistenciaRequest req) {
        Empleado emp = empleadoRepository.findById(req.getEmpleadoId()).orElse(null);
        if (emp == null) return ResponseEntity.badRequest().build();

        Asistencia a = new Asistencia();
        a.setEmpleado(emp);
        a.setFecha(java.time.LocalDate.now().toString());
        a.setHoraEntrada(horaActual());
        a.setEstado(req.getEstado());
        a.setJustificacion(req.getJustificacion());
        asistenciaRepository.save(a);
        return ResponseEntity.ok(toMap(a));
    }

    // PUT /api/asistencia/{id}  → registrar salida
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id,
                                                           @RequestBody AsistenciaRequest req) {
        return asistenciaRepository.findById(id).map(a -> {
            a.setHoraSalida(horaActual());
            if (req.getEstado() != null) a.setEstado(req.getEstado());
            asistenciaRepository.save(a);
            return ResponseEntity.ok(toMap(a));
        }).orElse(ResponseEntity.notFound().build());
    }

    private String horaActual() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private Map<String, Object> toMap(Asistencia a) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",             a.getId());
        map.put("empleadoId",     a.getEmpleado().getId());
        map.put("nombreEmpleado", a.getEmpleado().getNombre() + " " + a.getEmpleado().getApellidos());
        map.put("fecha",          a.getFecha());
        map.put("horaEntrada",    a.getHoraEntrada());
        map.put("horaSalida",     a.getHoraSalida());
        map.put("estado",         a.getEstado());
        map.put("justificacion",  a.getJustificacion());
        return map;
    }
}
