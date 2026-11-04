package queueup.api.auth.dtos;

public record RegisterResponseDto(
        String name,
        String email,
        String role,
        Long businessId) {

}
