package queueup.api.controllers;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import queueup.api.dtos.responses.DashboardMetricsDto;
import queueup.api.services.MetricsService;
import queueup.api.shared.apiResponse.ApiResponse;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<DashboardMetricsDto>> getMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now(ZoneOffset.UTC);
        return ResponseEntity.ok(
                ApiResponse.success("Metrics fetched", metricsService.getDashboardMetrics(target), HttpStatus.OK));
    }
}
