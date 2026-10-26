package queueup.api.dtos.responses;

public record CategoryMetricsDto(
        Long categoryId,
        String categoryName,
        Long servedCount,
        Double avgWaitTimeMinutes) {

}
