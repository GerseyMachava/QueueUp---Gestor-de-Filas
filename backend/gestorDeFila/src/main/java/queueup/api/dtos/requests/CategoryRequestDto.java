package queueup.api.dtos.requests;

public record CategoryRequestDto(
    String name,
    String defaultPriority,
    int averageServiceTimeMinutes,
    String prefix,
    Long businessId



) {

}
