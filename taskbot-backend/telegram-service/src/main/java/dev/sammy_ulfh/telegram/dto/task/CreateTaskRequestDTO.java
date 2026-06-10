package dev.sammy_ulfh.telegram.dto.task;

import java.time.LocalDate;

public class CreateTaskRequestDTO {
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private LocalDate fechaLimite;
    private Integer tiempoEstimado;
    private Long idProyecto;
    private Long idSprint;
    private Long idEstado;
    private Long idPrioridad;
    private Integer complejidad;

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

    public Long getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Long idProyecto) { this.idProyecto = idProyecto; }

    public Long getIdSprint() { return idSprint; }
    public void setIdSprint(Long idSprint) { this.idSprint = idSprint; }

    public Long getIdEstado() { return idEstado; }
    public void setIdEstado(Long idEstado) { this.idEstado = idEstado; }

    public Long getIdPrioridad() { return idPrioridad; }
    public void setIdPrioridad(Long idPrioridad) { this.idPrioridad = idPrioridad; }

    public Integer getComplejidad() { return complejidad; }
    public void setComplejidad(Integer complejidad) { this.complejidad = complejidad; }
}
