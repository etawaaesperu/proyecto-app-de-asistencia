package com.asistencia.asistencia_api.dto;

public class UsuarioData {
    private Long id;
    private String nombre;
    private String apellido;
    private String rol;
    private Long idEmpleado;

    public UsuarioData(Long id, String nombre, String apellido, String rol, Long idEmpleado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.idEmpleado = idEmpleado;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRol() { return rol; }
    public Long getIdEmpleado() { return idEmpleado; }
}
