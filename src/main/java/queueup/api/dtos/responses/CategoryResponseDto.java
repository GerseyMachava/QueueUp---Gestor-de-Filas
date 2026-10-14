package queueup.api.dtos.responses;

public record CategoryResponseDto(
    String name,
    String defaultPriority,
    int averageServiceTimeMinutes,
    String prefix,
    Long businessId

) {

}
