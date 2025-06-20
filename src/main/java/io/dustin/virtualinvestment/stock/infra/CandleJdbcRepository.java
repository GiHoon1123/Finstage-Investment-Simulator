package io.dustin.virtualinvestment.stock.infra;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CandleJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(String tableName, CandleEntity candle) {
        String sql = """
                INSERT INTO %s (timestamp, open_price, high_price, low_price, close_price, volume)
                VALUES (?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE 
                    open_price=?, high_price=?, low_price=?, close_price=?, volume=?""".formatted(tableName);

        jdbcTemplate.update(sql,
                candle.getTimestamp(), candle.getOpenPrice(), candle.getHighPrice(),
                candle.getLowPrice(), candle.getClosePrice(), candle.getVolume(),
                candle.getOpenPrice(), candle.getHighPrice(), candle.getLowPrice(),
                candle.getClosePrice(), candle.getVolume()
        );
    }

    public List<CandleEntity> findByRange(String tableName, Instant start, Instant end) {
        String sql = """
                SELECT * FROM %s
                WHERE timestamp BETWEEN ? AND ?
                ORDER BY timestamp ASC""".formatted(tableName);

        RowMapper<CandleEntity> rowMapper = (rs, rowNum) -> new CandleEntity(
                rs.getLong("timestamp"),
                rs.getDouble("open_price"),
                rs.getDouble("high_price"),
                rs.getDouble("low_price"),
                rs.getDouble("close_price"),
                rs.getLong("volume")
        );

        return jdbcTemplate.query(sql, rowMapper,
                Timestamp.from(start),
                Timestamp.from(end)
        );
    }
}

