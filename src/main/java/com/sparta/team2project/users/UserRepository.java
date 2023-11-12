package com.sparta.team2project.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    Optional<Users> findByKakaoId(Long kakaoId);

    boolean existsByNickName(String nickName);

    Optional<Users> findByNickName(String nickName);
}
