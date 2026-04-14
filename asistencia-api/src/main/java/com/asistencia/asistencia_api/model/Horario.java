package com.asistencia.asistencia_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    private String fecha;
    private String horaEntrada;
    private String horaSalida;
    private String observaciones;

    public Horario() {}

    public Long getId() { return id; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }
    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
