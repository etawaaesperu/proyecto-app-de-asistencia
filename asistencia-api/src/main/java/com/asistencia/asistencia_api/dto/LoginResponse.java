package com.asistencia.asistencia_api.dto;

public class LoginResponse {
    private boolean success;
    private String mensaje;
    private UsuarioData usuario;

    public LoginResponse(boolean success, String mensaje, UsuarioData usuario) {
        this.success = success;
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public boolean isSuccess() { return success; }
    public String getMensaje() { return mensaje; }
    public UsuarioData getUsuario() { return usuario; }
}
