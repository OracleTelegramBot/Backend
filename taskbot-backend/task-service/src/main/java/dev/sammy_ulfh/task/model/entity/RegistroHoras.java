package dev.sammy_ulfh.task.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "REGISTRO_HORAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroHoras {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_registro_horas")
    @SequenceGenerator(name = "seq_registro_horas", sequenceName = "SEQ_REGISTRO_HORAS", allocationSize = 1)
    @Column(name = "ID_REGISTRO")
    private Long idRegistro;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "HORAS_TRABAJADAS", nullable = false, precision = 4, scale = 2)
    private BigDecimal horasTrabajadas;

    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    @Column(name = "ID_TAREA", nullable = false)
    private Long idTarea;
}