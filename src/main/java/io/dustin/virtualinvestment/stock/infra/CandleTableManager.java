package io.dustin.virtualinvestment.stock.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CandleTableManager {

    private final JdbcTemplate jdbcTemplate;

    public void createTableIfNotExists(String tableName) {
        String sql = """
                CREATE TABLE IF NOT EXISTS %s (
                    timestamp BIGINT NOT NULL PRIMARY KEY,
                    open_price DOUBLE NOT NULL,
                    high_price DOUBLE NOT NULL,
                    low_price DOUBLE NOT NULL,
                    close_price DOUBLE NOT NULL,
                    volume BIGINT NOT NULL
                )""".formatted(tableName);

        jdbcTemplate.execute(sql);
    }

}
