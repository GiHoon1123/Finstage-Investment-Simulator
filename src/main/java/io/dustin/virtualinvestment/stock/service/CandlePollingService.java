package io.dustin.virtualinvestment.stock.service;

import io.dustin.virtualinvestment.stock.domain.Interval;
import io.dustin.virtualinvestment.stock.domain.Candle;
import io.dustin.virtualinvestment.common.config.PollingTargetProperties;
import io.dustin.virtualinvestment.stock.infra.YahooFinanceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
                    List<Candle> candles = yahooFinanceClient.fetchCandles(target.symbol(), interval);
                    log.info("🔔 Broadcasting AAPL {}: {}건", interval, candles.size());
                    webSocketService.broadcast(target.symbol(), interval, candles);
                    log.info("✅ {} {} 전송 완료 ({}개)", target.symbol(), interval, candles.size());
                } catch (Exception e) {
                    log.warn("⚠️ {} {} 전송 실패: {}", target.symbol(), interval, e.getMessage());
                }
            }
        }
    }
}
