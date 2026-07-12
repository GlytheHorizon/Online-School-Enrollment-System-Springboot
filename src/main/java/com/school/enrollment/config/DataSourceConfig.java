package com.school.enrollment.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties props) {
        String host = env("MYSQLHOST", "localhost");
        String port = env("MYSQLPORT", "3306");
        String db = env("MYSQLDATABASE", "school_enrollment");
        String user = env("MYSQLUSER", "root");
        String password = env("MYSQLPASSWORD", "");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        HikariDataSource ds = props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }

    private static String env(String key, String fallback) {
        String val = System.getenv(key);
        return val != null && !val.isEmpty() ? val : fallback;
    }
}
