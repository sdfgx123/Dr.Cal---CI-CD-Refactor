package com.fc.mini3server._core.utils;

import com.fc.mini3server.domain.Work;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
public class QueryDSLSupport extends QuerydslRepositorySupport {

    public QueryDSLSupport(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        super(Work.class);
    }

    public Page<Work> makePagination(Pageable pageable, JPQLQuery<Work> query, Long total) {
        log.info("makePagination START");
        log.info("pageable : " + pageable + ", query : " + query + ", total : " + total);
        Querydsl querydsl = getQuerydsl();
        log.info("querydsl : " + querydsl);
        List<Work> results = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(results, pageable, total);
    }
}
