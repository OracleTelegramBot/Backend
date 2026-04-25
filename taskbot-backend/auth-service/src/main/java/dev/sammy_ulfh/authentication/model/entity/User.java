package dev.sammy_ulfh.authentication.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "Usuario")
public class User {
    
    @Id
    @Column(name = "id_usuario")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", allocationSize = 50)
    private Long id;
    
    @Column(name = "correo", nullable = false, unique = true)
    @NotBlank(message = "Correo cannot be blank")
    @Email(message = "Correo should be valid")
    private String email;
    
    @Column(name = "contrasena", nullable = false)
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @Column(name = "nombre", nullable = false)
    private String firstName;
    
    @Column(name = "apellido", nullable = false)
    private String lastName;
    
    @Column(name = "id_rol", nullable = false)
    private Long idRol;
    
    @Column(name = "id_equipo")
    private Long idEquipo;
    
    public User() {}
    
    public User(Long id, String email, String password, String firstName, String lastName, Long idRol, Long idEquipo) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idRol = idRol;
        this.idEquipo = idEquipo;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Long getIdRol() {
        return idRol;
    }
    
    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", idRol=" + idRol +
                ", idEquipo=" + idEquipo +
                '}';
    }
}
