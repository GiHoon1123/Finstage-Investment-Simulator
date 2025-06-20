package io.dustin.virtualinvestment.stock.infra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class CandleEntity {
    private final long timestamp;
    private final double openPrice;
    private final double highPrice;
    private final double lowPrice;
    private final double closePrice;
    private final long volume;
}
