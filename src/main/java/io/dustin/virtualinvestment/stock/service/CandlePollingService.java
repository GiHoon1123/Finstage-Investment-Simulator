package io.dustin.virtualinvestment.stock.service;

import io.dustin.virtualinvestment.common.config.PollingTargetProperties;
import io.dustin.virtualinvestment.stock.domain.Candle;
import io.dustin.virtualinvestment.stock.domain.Interval;
import io.dustin.virtualinvestment.stock.dto.CandleResponse;
import io.dustin.virtualinvestment.stock.dto.ExternalCandleResponse;
import io.dustin.virtualinvestment.stock.infra.CandleEntity;
import io.dustin.virtualinvestment.stock.infra.CandleJdbcRepository;
import io.dustin.virtualinvestment.stock.infra.CandleTableManager;
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
    private final CandleJdbcRepository candleJdbcRepository;
    private final CandleTableManager candleTableManager;

    @Scheduled(fixedRate = 1000) // 1초마다 polling
    public void pollAllTargets() {
        log.info("⏰ Yahoo API polling 시작");

        for (PollingTargetProperties.Target target : pollingTargetProperties.getTargets()) {
            String symbol = target.symbol().toLowerCase();

            for (Interval interval : target.intervals()) {
                try {
                    ExternalCandleResponse externalResponse =
                            yahooFinanceClient.fetchRawCandles(symbol, interval);

                    var result = externalResponse.getChart().getResult().getFirst();
                    var timestamps = result.getTimestamp();
                    var quote = result.getIndicators().getQuote().getFirst();

                    List<CandleResponse> responseList = new ArrayList<>();

                    for (int i = 0; i < timestamps.size(); i++) {
                        Long timestamp  = timestamps.get(i);
                        Double open = quote.getOpen().get(i);
                        Double high = quote.getHigh().get(i);
                        Double low = quote.getLow().get(i);
                        Double close = quote.getClose().get(i);
                        Long volume = quote.getVolume().get(i);

                        if (timestamp == null || open == null || high == null ||
                                low == null || close == null || volume == null) {
                            continue; // null 값 포함된 데이터는 스킵
                        }

                        Candle candle = Candle.from(symbol, interval.getValue(), timestamp, open, high, low, close, volume);
                        CandleEntity entity = new CandleEntity(
                                candle.timestamp(),
                                candle.open(),
                                candle.high(),
                                candle.low(),
                                candle.close(),
                                candle.volume()
                        );

                        // 테이블명 구성
                        String tableName = symbol + "_" + interval.getValue();

                        // 테이블 없으면 생성
                        candleTableManager.createTableIfNotExists(tableName);

                        // 저장
                        candleJdbcRepository.save(tableName, entity);

                        // 전송 목록에 추가
                        responseList.add(CandleResponse.from(symbol, interval.getValue(), candle));
                    }

                    log.info("🔔 Broadcasting {} {}: {}건", symbol, interval, responseList.size());
                    webSocketService.broadcast(symbol, interval, responseList);
                    log.info("✅ {} {} 전송 완료 ({}개)", symbol, interval, responseList.size());

                } catch (Exception e) {
                    log.warn("⚠️ {} {} 전송 실패: {}", target.symbol(), interval, e.getMessage());
                }
            }
        }
    }
}
