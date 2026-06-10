package dev.sammy_ulfh.telegram.repository;

import dev.sammy_ulfh.telegram.entity.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Sesiones de Telegram almacenadas en Redis.
 * Key format: "telegram:session:<chatId>"
 * Reemplaza el antiguo ConcurrentHashMap en memoria.
 */
@Repository
public class SessionRepository {

    private static final String KEY_PREFIX = "telegram:session:";

    @Value("${telegram.session.ttl-hours:24}")
    private long ttlHours;

    @Autowired
    private RedisTemplate<String, UserSession> userSessionRedisTemplate;

    public UserSession get(Long chatId) {
        return userSessionRedisTemplate.opsForValue().get(key(chatId));
    }

    /** Guarda o actualiza la sesión. Refresca el TTL en cada llamada (sliding window). */
    public void save(Long chatId, UserSession session) {
        userSessionRedisTemplate.opsForValue().set(
                key(chatId),
                session,
                Duration.ofHours(ttlHours)
        );
    }

    public void delete(Long chatId) {
        userSessionRedisTemplate.delete(key(chatId));
    }

    public boolean exists(Long chatId) {
        return Boolean.TRUE.equals(userSessionRedisTemplate.hasKey(key(chatId)));
    }

    public void touch(Long chatId) {
        userSessionRedisTemplate.expire(key(chatId), ttlHours, TimeUnit.HOURS);
    }

    private String key(Long chatId) {
        return KEY_PREFIX + chatId;
    }
}
