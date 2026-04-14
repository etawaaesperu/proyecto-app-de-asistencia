package com.asistencia.asistencia_api.controller;

import com.asistencia.asistencia_api.dto.HorarioRequest;
import com.asistencia.asistencia_api.model.Empleado;
import com.asistencia.asistencia_api.model.Horario;
import com.asistencia.asistencia_api.repository.EmpleadoRepository;
import com.asistencia.asistencia_api.repository.HorarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioRepository horarioRepository;
    private final EmpleadoRepository empleadoRepository;

    public HorarioController(HorarioRepository horarioRepository,
                              EmpleadoRepository empleadoRepository) {
        this.horarioRepository  = horarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Horario> lista = horarioRepository.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Horario h : lista) resultado.add(toMap(h));
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody HorarioRequest req) {
        Empleado emp = empleadoRepository.findById(req.getEmpleadoId()).orElse(null);
        if (emp == null) return ResponseEntity.badRequest().build();

        Horario h = new Horario();
        h.setEmpleado(emp);
        h.setFecha(req.getFecha());
        h.setHoraEntrada(req.getHoraEntrada());
        h.setHoraSalida(req.getHoraSalida());
        h.setObservaciones(req.getObservaciones());
        horarioRepository.save(h);
        return ResponseEntity.ok(toMap(h));
    }

    private Map<String, Object> toMap(Horario h) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",             h.getId());
        map.put("empleadoId",     h.getEmpleado().getId());
        map.put("nombreEmpleado", h.getEmpleado().getNombre() + " " + h.getEmpleado().getApellidos());
        map.put("fecha",          h.getFecha());
        map.put("horaEntrada",    h.getHoraEntrada());
        map.put("horaSalida",     h.getHoraSalida());
        map.put("observaciones",  h.getObservaciones());
        return map;
    }
}
