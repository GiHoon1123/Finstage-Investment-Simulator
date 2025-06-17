package io.dustin.virtualinvestment.stock.service;

import io.dustin.virtualinvestment.stock.domain.Interval;
import io.dustin.virtualinvestment.stock.domain.Candle;
import io.dustin.virtualinvestment.common.config.PollingTargetProperties;
import io.dustin.virtualinvestment.stock.dto.CandleResponse;
import io.dustin.virtualinvestment.stock.dto.ExternalCandleResponse;
import io.dustin.virtualinvestment.stock.infra.YahooFinanceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandlePollingService {

    private final PollingTargetProperties pollingTargetProperties;
    private final YahooFinanceClient yahooFinanceClient;
    private final WebSocketService webSocketService;

    // 배치: 60초마다 polling 수행
    @Scheduled(fixedRate = 1000)
    public void pollAllTargets() {
        log.info("⏰ Yahoo API polling 시작");

        for (PollingTargetProperties.Target target : pollingTargetProperties.getTargets()) {
            for (Interval interval : target.intervals()) {
                try {
                    ExternalCandleResponse externalResponse = yahooFinanceClient.fetchRawCandles(target.symbol(), interval);

                    var result = externalResponse.getChart().getResult().getFirst();
                    var timestamps = result.getTimestamp();
                    var quote = result.getIndicators().getQuote().getFirst();

                    List<CandleResponse> responseList = new ArrayList<>();

                    for (int i = 0; i < timestamps.size(); i++) {
                        Long ts = timestamps.get(i);
                        Double open = quote.getOpen().get(i);
                        Double high = quote.getHigh().get(i);
                        Double low = quote.getLow().get(i);
                        Double close = quote.getClose().get(i);
                        Long volume = quote.getVolume().get(i);

                        List<String> nullFields = new ArrayList<>();

                        if (ts == null) nullFields.add("timestamp");
                        if (open == null) nullFields.add("open");
                        if (high == null) nullFields.add("high");
                        if (low == null) nullFields.add("low");
                        if (close == null) nullFields.add("close");
                        if (volume == null) nullFields.add("volume");

                        if (!nullFields.isEmpty()) {
                            System.out.println("Skipped index " + i + " due to null fields: " + String.join(", ", nullFields));
                            continue;
                        }

                        Instant timestamp = Instant.ofEpochSecond(ts);
                        Candle candle = new Candle(target.symbol(), interval.getValue(), timestamp, open, high, low, close, volume);
                        responseList.add(CandleResponse.from(target.symbol(), interval.getValue(), candle));
                    }

                    log.info("🔔 Broadcasting {} {}: {}건", target.symbol(), interval, responseList.size());
                    webSocketService.broadcast(target.symbol(), interval, responseList);
                    log.info("✅ {} {} 전송 완료 ({}개)", target.symbol(), interval, responseList.size());
                } catch (Exception e) {
                    log.warn("⚠️ {} {} 전송 실패: {}", target.symbol(), interval, e.getMessage());
                }
            }
        }
    }
}
