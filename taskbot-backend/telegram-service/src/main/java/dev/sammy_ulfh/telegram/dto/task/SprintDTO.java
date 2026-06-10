package dev.sammy_ulfh.telegram.dto.task;

import java.time.LocalDate;

public class SprintDTO {
    private Long idSprint;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long idProyecto;

    public Long getIdSprint() { return idSprint; }
    public void setIdSprint(Long idSprint) { this.idSprint = idSprint; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Long getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Long idProyecto) { this.idProyecto = idProyecto; }
}
