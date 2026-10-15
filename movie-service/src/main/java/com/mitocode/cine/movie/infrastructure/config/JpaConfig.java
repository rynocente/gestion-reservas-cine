package com.mitocode.cine.movie.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.sql.DataSource;

/**
 * Configuración de JPA y Flyway — vive en infrastructure porque
 * son detalles de implementación (tecnologías externas).
 *
 * En Spring Boot 4, la autoconfiguración de Flyway fue movida a un módulo
 * separado, por lo que spring.flyway.* no es reconocida automáticamente.
 * Se configura Flyway explícitamente como @Bean para garantizar que las
 * migraciones se ejecuten durante el arranque del contexto Spring.
 *
 * initMethod = "migrate": Spring llama a flyway.migrate() después de crear
 * el bean, lo que ejecuta todos los scripts pendientes en db/migration.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
    }
}
