package com.sparta.team2project.commons.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    public String getData(String key) { // key를 통해 value(데이터)를 얻는다.
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setDataExpire(String key, String value, long duration) {
        //  duration 동안 (key, value)를 저장한다.
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public void deleteData(String key) {
        // 데이터 삭제
        redisTemplate.delete(key);
    }

    public void saveRefreshToken(String email, String refreshToken) {
        String key = "refreshToken:" + email; // 사용자 별로 고유한 키 생성
        setDataExpire(key, refreshToken, 2 * 7 * 24 * 60 * 60 * 1000L); // 리프레시 토큰 저장 및 만료 시간 설정
    }
    public String getRefreshToken(String email) {
        String key = "refreshToken:" + email; // 사용자 별로 고유한 키 생성
        return getData(key); // 저장된 리프레시 토큰 가져오기
    }

    public void deleteRefreshToken(String email) {
        String key = "refreshToken:" + email; // 이메일을 사용하여 키 생성
        // 레디스에서 해당 키를 사용하여 리프레시 토큰을 삭제
        redisTemplate.delete(key);
    }
}