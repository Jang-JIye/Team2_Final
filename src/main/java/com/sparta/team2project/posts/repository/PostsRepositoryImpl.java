package com.sparta.team2project.posts.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.team2project.posts.entity.Posts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.sparta.team2project.posts.entity.QPosts.posts;
import static com.sparta.team2project.schedules.entity.QSchedules.schedules;
import static com.sparta.team2project.tags.entity.QTags.tags;
import static com.sparta.team2project.tripdate.entity.QTripDate.tripDate;

@RequiredArgsConstructor
public class PostsRepositoryImpl implements PostsRepositoryCustom {

    private final JPAQueryFactory factory;


    @Override
    public Page<Posts> findAllPosts(Pageable pageable) {
        List<Posts> result = factory
                .select(posts)
                .from(posts)
                .orderBy(posts.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = factory
                .select(posts.count()).from(posts)
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }
    @Override
    public Set<Posts> SearchKeyword(String keyword){
        BooleanExpression titleContains = posts.title.contains(keyword);
        BooleanExpression tagsContains = tags.purpose.contains(keyword);
        BooleanExpression placeNameContains = schedules.placeName.contains(keyword);
        BooleanExpression contentsContains = schedules.contents.contains(keyword);

        Set<Posts> results= new HashSet<>(factory.selectFrom(posts)
                .leftJoin(tags).on(tags.posts.eq(posts)).fetchJoin()
                .leftJoin(tripDate).on(tripDate.posts.eq(posts)).fetchJoin()
                .leftJoin(schedules).on(schedules.tripDate.eq(tripDate)).fetchJoin()
                .where(titleContains.or(tagsContains).or(placeNameContains).or(contentsContains))
                .fetch());
        return results;
    }
}
