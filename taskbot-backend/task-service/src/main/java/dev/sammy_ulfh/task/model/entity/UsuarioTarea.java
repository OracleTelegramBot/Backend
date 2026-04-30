package dev.sammy_ulfh.task.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USUARIO_TAREA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioTarea {

    @EmbeddedId
    private UsuarioTareaId id;
}