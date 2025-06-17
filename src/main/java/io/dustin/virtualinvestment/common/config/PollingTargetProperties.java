package io.dustin.virtualinvestment.common.config;


import io.dustin.virtualinvestment.stock.domain.Interval;
import io.dustin.virtualinvestment.stock.domain.StockType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * application.yml에서 실시간으로 수집할 종목과 주기 설정을 불러옵니다.
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "polling")
public class PollingTargetProperties {

    private final List<Target> targets;

    public PollingTargetProperties(List<Target> targets) {
        this.targets = targets;
    }

    public record Target(String symbol, StockType type, List<Interval> intervals) {
    }
}


