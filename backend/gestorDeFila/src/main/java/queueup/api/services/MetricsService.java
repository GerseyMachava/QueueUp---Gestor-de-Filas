package queueup.api.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import queueup.api.auth.Services.AuthService;
import queueup.api.dtos.responses.CategoryMetricsDto;
import queueup.api.dtos.responses.DashboardMetricsDto;
import queueup.api.entities.enums.Status;
import queueup.api.repositories.TicketRepository;

@Service
@AllArgsConstructor
public class MetricsService {

    private final AuthService authService;
    private final TicketRepository ticketRepository;

    public DashboardMetricsDto getDashboardMetrics(LocalDate date) {
        long businessId = authService.getLoggedInBusinessId();

        Instant start = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        long totalServed = ticketRepository.countByBusinessAndStatusAndFinishedAtBetween(businessId, Status.COMPLETED,
                start, end);
        long currentlyWainting = ticketRepository.countByBusinessIdAndStatus(businessId, Status.COMPLETED);

        Double avgWaitRaw = ticketRepository.avgWaitTimeMinutes(businessId, start, end);
        Double avgWait = avgWaitRaw == null ? null : Math.round(avgWaitRaw * 10.0) / 10.0;

        Double abandonmentRaw = ticketRepository.abandonmentRate(
                businessId, Status.ABANDONED, start, end);

        double abandonamentRate = abandonmentRaw == null ? 0.0 : Math.round(abandonmentRaw * 10.0) / 10.0;

        List<Object[]> rawRows = ticketRepository.getCategoryMetricsRaw(businessId, start, end);
        List<CategoryMetricsDto> byCatehory = rawRows.stream()
                .map(row -> new CategoryMetricsDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        row[3] == null ? null : ((Number) row[3]).doubleValue()))
                .toList();

        return new DashboardMetricsDto(
                date, totalServed, currentlyWainting, avgWait, abandonamentRate, byCatehory);
    }
}
