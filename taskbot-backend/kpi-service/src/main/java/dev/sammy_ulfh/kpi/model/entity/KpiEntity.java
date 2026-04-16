package dev.sammy_ulfh.kpi.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "KPI")
@Data
public class KpiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_seq_gen")
    @SequenceGenerator(name = "kpi_seq_gen", sequenceName = "KPI_SEQ", allocationSize = 1)
    @Column(name = "ID_KPI")
    private Long idKpi;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "VALOR")
    private Double valor;

    @Column(name = "FECHA_CALCULO")
    @Temporal(TemporalType.DATE)
    private java.util.Date fechaCalculo;

    @Column(name = "ID_PROYECTO")
    private Long idProyecto;

    @Column(name = "ID_USUARIO")
    private Long idUsuario;
}