package io.dustin.virtualinvestment.stock.dto;


import io.dustin.virtualinvestment.stock.domain.Candle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "캔들 데이터 응답 DTO")
public class CandleResponse {

    @Schema(description = "종목 심볼", example = "AAPL")
    private final String symbol;

    @Schema(description = "차트 간격", example = "1m")
    private final String interval;

    @Schema(description = "타임스탬프 (UTC)", example = "2025-06-17T00:00:00Z")
    private final Instant timestamp;

    @Schema(description = "시가", example = "189.43")
    private final double open;

    @Schema(description = "고가", example = "191.12")
    private final double high;

    @Schema(description = "저가", example = "188.76")
    private final double low;

    @Schema(description = "종가", example = "190.45")
    private final double close;

    @Schema(description = "거래량", example = "28937210")
    private final long volume;

    public static CandleResponse from(String symbol, String interval, Candle candle) {
        return CandleResponse.builder()
                .symbol(symbol)
                .interval(interval)
                .timestamp(candle.timestamp())
                .open(candle.open())
                .high(candle.high())
                .low(candle.low())
                .close(candle.close())
                .volume(candle.volume())
                .build();
    }
}
