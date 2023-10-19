package com.sparta.team2project.profile.repository;

import com.sparta.team2project.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository <Profile, Long>{
    Optional<Profile> findByUsers_Email(String email);
}
