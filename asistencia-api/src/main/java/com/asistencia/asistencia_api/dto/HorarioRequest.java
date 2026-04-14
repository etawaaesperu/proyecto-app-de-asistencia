package com.asistencia.asistencia_api.dto;

public class HorarioRequest {
    private Long empleadoId;
    private String fecha;
    private String horaEntrada;
    private String horaSalida;
    private String observaciones;

    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }
    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
