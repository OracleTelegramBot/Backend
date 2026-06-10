package dev.sammy_ulfh.telegram.dto.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDTO {
    private Long idTarea;
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private LocalDate fechaLimite;
    private Integer tiempoEstimado;
    private Integer tiempoReal;
    private Long idProyecto;
    private Long idSprint;
    private Long idEstado;
    private Long idPrioridad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer complejidad;

    public Long getIdTarea() { return idTarea; }
    public void setIdTarea(Long idTarea) { this.idTarea = idTarea; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public Integer getTiempoEstimado() { return tiempoEstimado; }
    public void setTiempoEstimado(Integer tiempoEstimado) { this.tiempoEstimado = tiempoEstimado; }

    public Integer getTiempoReal() { return tiempoReal; }
    public void setTiempoReal(Integer tiempoReal) { this.tiempoReal = tiempoReal; }

    public Long getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Long idProyecto) { this.idProyecto = idProyecto; }

    public Long getIdSprint() { return idSprint; }
    public void setIdSprint(Long idSprint) { this.idSprint = idSprint; }

    public Long getIdEstado() { return idEstado; }
    public void setIdEstado(Long idEstado) { this.idEstado = idEstado; }

    public Long getIdPrioridad() { return idPrioridad; }
    public void setIdPrioridad(Long idPrioridad) { this.idPrioridad = idPrioridad; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public Integer getComplejidad() { return complejidad; }
    public void setComplejidad(Integer complejidad) { this.complejidad = complejidad; }
}
