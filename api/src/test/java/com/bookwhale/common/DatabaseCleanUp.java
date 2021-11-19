package com.bookwhale.common;

import com.google.common.base.CaseFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseCleanUp implements InitializingBean {

    @PersistenceContext
    private EntityManager em;

    private List<String> tables;

    @Override
    public void afterPropertiesSet() {
        tables = em.getMetamodel().getEntities().stream()
            .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
            .map(entityType -> {
                // DB Table 명이 Entity Class명과 일치하지 않아 @Table이 사용된 경우 @Table의 name을 사용
                Optional<Table> atTable = Optional.ofNullable(
                    entityType.getJavaType().getAnnotation(Table.class));
                return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                    atTable.isPresent() ? atTable.get().name() : entityType.getName());
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void cleanUp() {
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (String table : tables) {
            em.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
