package io.dustin.virtualinvestment.stock.domain;

import java.time.Instant;

/**
 * Candle
 * <p>
 * 주어진 시점의 주가 정보를 나타내는 값 객체(Value Object)
 */
public record Candle(
        String symbol,          // 종목 심볼 (e.g., "AAPL")
        String interval,        // 차트 간격 (e.g., "1m")
        Long timestamp,      // 캔들의 기준 시간 (UTC)
        double open,            // 시가
        double high,            // 고가
        double low,             // 저가
        double close,           // 종가
        long volume             // 거래량
) {


    /**
     * Instant를 직접 받아서 Candle 생성
     */
    public static Candle from(String symbol, String interval, Long timestamp,
                            double open, double high, double low, double close, long volume) {
        return new Candle(symbol, interval, timestamp, open, high, low, close, volume);
    }
}
