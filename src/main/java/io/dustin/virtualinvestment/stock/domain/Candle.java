package io.dustin.virtualinvestment.stock.domain;

import lombok.Getter;
import java.time.Instant;

/**
 * Candle
 * <p>
 * 이 클래스는 특정 시점의 주가 정보를 나타내는 값 객체(Value Object)입니다.
 * ✅ Java 16의 record를 사용하여 구현한 이유:
 * - Candle은 불변(immutable)이며, 동등성은 "값"에 기반합니다. 즉, 값이 같으면 같은 객체로 취급됩니다.
 * - record는 다음과 같은 특징을 가집니다:
 * 1. 모든 필드에 대한 `equals()` / `hashCode()` / `toString()` 자동 생성
 * → 값 객체에서 필요한 비교, 해시, 디버깅 로직을 자동으로 제공하여 실수를 줄이고 생산성을 높입니다.
 * 2. 생성자와 getter도 자동 생성됨 (`timestamp()` 형태)
 * 3. 불변 객체(필드가 final)로 설계되어 있어 멀티스레드 환경에서 안전하게 사용할 수 있음
 * 이 Candle 객체는 주식의 1개 캔들(시가, 고가, 저가, 종가, 거래량, 시간)을 표현합니다.
 */
public record Candle(
        String symbol,          // 종목 심볼 (e.g., "AAPL")
        String interval,        // 차트 간격 (e.g., "1m")
        Instant timestamp,      // 캔들의 기준 시간 (UTC)
        double open,            // 시가
        double high,            // 고가
        double low,             // 저가
        double close,           // 종가
        long volume             // 거래량
) {
}
