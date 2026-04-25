/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.sammy_ulfh.kpi.repository;

/**
 *
 * @author sammy
 */
import dev.sammy_ulfh.kpi.model.entity.AnuncioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnuncioRepository extends JpaRepository<AnuncioEntity, Long> {
    // Al heredar de JpaRepository, ya se tiene acceso a save(), findAll(),
    // findById(), etc
    // No es necesario escribir nada para cumplir con un guardado normal o basico
}