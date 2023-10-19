package com.sparta.team2project.pictures.entity;

import com.sparta.team2project.schedules.entity.Schedules;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Entity
@Table(name = "pictures")
@Getter
@NoArgsConstructor
public class Pictures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String picturesURL;

    @Column(nullable = true)
    private String picturesName;

    @Column(nullable = true)
    private String pictureContentType;

    @Column(nullable = true)
    private Long pictureSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedules_id",nullable = false)
    private Schedules schedules;

    public Pictures(
            Schedules schedules,
            String picturesURL,
            String picturesName,
            String pictureContentType,
            Long pictureSize
    ){
        this.schedules = schedules;
        this.picturesURL = picturesURL;
        this.picturesName = picturesName;
        this.pictureContentType = pictureContentType;
        this.pictureSize = pictureSize;
    }

    public void updatePictures(
            String picturesURL,
            String picturesName,
            String pictureContentType,
            Long pictureSize
    ){
        this.picturesURL = picturesURL;
        this.picturesName = picturesName;
        this.pictureContentType = pictureContentType;
        this.pictureSize = pictureSize;
    }
}
