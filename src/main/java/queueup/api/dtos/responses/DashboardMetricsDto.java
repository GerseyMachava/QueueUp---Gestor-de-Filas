package queueup.api.dtos.responses;

import java.time.LocalDate;
import java.util.List;

public record DashboardMetricsDto(
        LocalDate date,
        long totalServedToday,
        long currentlyWaiting,
        Double avgWaitTimeMinutes,
        double abandonmentRatePercent,
        List<CategoryMetricsDto> byCategory) {

}
