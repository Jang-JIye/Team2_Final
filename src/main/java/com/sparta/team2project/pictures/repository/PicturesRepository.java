package com.sparta.team2project.pictures.repository;

import com.sparta.team2project.pictures.entity.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PicturesRepository extends JpaRepository<Pictures, Long> {
    void deleteByPicturesName(String picturesName);
}
