//ai-service/src/main/java/dev/sammy_ulfh/ai/repository/UserRepository.java
package dev.sammy_ulfh.ai.repository;

import dev.sammy_ulfh.ai.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByNombre(String nombre);
}