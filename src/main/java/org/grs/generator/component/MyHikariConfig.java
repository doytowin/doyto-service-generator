package org.grs.generator.component;

import java.sql.SQLException;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 类描述
 *
 * @author Yuan Zhen on 2016-08-11.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class MyHikariConfig extends HikariConfig {
    @Bean
    public DataSource dataSource() throws SQLException {
        return new HikariDataSource(this);
    }

}