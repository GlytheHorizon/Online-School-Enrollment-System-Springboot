package com.school.enrollment.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

@Configuration
@ConditionalOnProperty("RAILWAY_SERVICE_NAME")
public class DataSourceConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource() {
        logEnvVars();
        RailwayDbConfig cfg = parseRailwayEnv();
        log.info("=== DataSource: JDBC URL={}", cfg.jdbcUrl);

        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl(cfg.jdbcUrl);
        hikari.setUsername(cfg.username);
        hikari.setPassword(cfg.password);
        hikari.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikari.setMaximumPoolSize(10);
        hikari.setConnectionTimeout(30000);
        hikari.setInitializationFailTimeout(-1);

        HikariDataSource ds = new HikariDataSource(hikari);
        retryConnect(ds);
        return ds;
    }

    private static final String DEFAULT_MYSQL_HOST = "MySQL.railway.internal";

    private RailwayDbConfig parseRailwayEnv() {
        String mysqlUrl = env("MYSQL_URL");
        if (mysqlUrl != null) {
            if (!mysqlUrl.startsWith("mysql://")) {
                log.warn("=== MYSQL_URL is set but does not start with 'mysql://', value={}. Falling back to individual MYSQL* vars.", mysqlUrl);
            } else {
                try {
                    URI uri = new URI(mysqlUrl);
                    String userInfo = uri.getUserInfo();
                    String host = uri.getHost();
                    int port = uri.getPort();
                    String path = uri.getPath();

                    if (userInfo == null) {
                        log.warn("=== MYSQL_URL={} is missing user info (user:password@host). Falling back to individual MYSQL* vars.", mysqlUrl);
                    } else if (host == null) {
                        log.warn("=== MYSQL_URL={} is missing a host. Falling back to individual MYSQL* vars.", mysqlUrl);
                    } else if (path == null || path.isEmpty() || path.equals("/")) {
                        log.warn("=== MYSQL_URL={} is missing a database name in the path. Falling back to individual MYSQL* vars.", mysqlUrl);
                    } else {
                        String[] up = userInfo.split(":", 2);
                        String user = up[0];
                        String pass = up.length > 1 ? up[1] : "";
                        String db = path.replace("/", "");
                        int resolvedPort = port > 0 ? port : 3306;
                        String jdbcUrl = String.format(
                            "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                            host, resolvedPort, db);
                        log.info("=== Successfully parsed MYSQL_URL. host={}, port={}, database={}", host, resolvedPort, db);
                        return new RailwayDbConfig(jdbcUrl, user, pass);
                    }
                } catch (URISyntaxException e) {
                    log.warn("=== Failed to parse MYSQL_URL={}. Falling back to individual MYSQL* vars.", mysqlUrl, e);
                }
            }
        } else {
            log.info("=== MYSQL_URL is not set. Falling back to individual MYSQL* vars.");
        }

        String host = env("MYSQLHOST", DEFAULT_MYSQL_HOST);
        String port = env("MYSQLPORT", "3306");
        String db = env("MYSQLDATABASE", "railway");
        String user = env("MYSQLUSER", "root");
        String pass = env("MYSQLPASSWORD");
        if (pass == null) pass = env("MYSQL_ROOT_PASSWORD", "");
        if (pass == null) pass = "";

        if (env("MYSQLHOST") == null) {
            log.warn("=== MYSQLHOST is not set, defaulting to Railway internal DNS name '{}'. " +
                "This only works if a MySQL service is deployed in the same Railway project/environment.", DEFAULT_MYSQL_HOST);
        }

        String jdbcUrl = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            host, port, db);
        return new RailwayDbConfig(jdbcUrl, user, pass);
    }

    private void retryConnect(HikariDataSource ds) {
        int maxAttempts = 5;
        for (int i = 1; i <= maxAttempts; i++) {
            try {
                ds.getConnection().close();
                log.info("=== MySQL connection successful!");
                return;
            } catch (Exception e) {
                log.warn("=== MySQL connection attempt {}/{} failed: {}", i, maxAttempts, e.getMessage());
                if (i < maxAttempts) {
                    try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                }
            }
        }
        log.error("=== MySQL connection failed after {} attempts", maxAttempts);
    }

    private void logEnvVars() {
        Map<String, String> vars = new TreeMap<>();
        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            String k = e.getKey();
            if (k.startsWith("MYSQL") || k.startsWith("RAILWAY") || k.startsWith("DB_") || k.startsWith("JDBC") || k.equals("PORT")) {
                vars.put(k, k.contains("PASSWORD") || k.contains("SECRET") ? "***" : e.getValue());
            }
        }
        log.info("=== Railway env vars: {}", vars);
    }

    private static String env(String key) {
        String val = System.getenv(key);
        return val != null && !val.isEmpty() ? val : null;
    }

    private static String env(String key, String fallback) {
        String val = System.getenv(key);
        return val != null && !val.isEmpty() ? val : fallback;
    }

    private static class RailwayDbConfig {
        final String jdbcUrl;
        final String username;
        final String password;
        RailwayDbConfig(String jdbcUrl, String username, String password) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
        }
    }
}
