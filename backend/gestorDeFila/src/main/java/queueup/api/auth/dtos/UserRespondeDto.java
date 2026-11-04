package queueup.api.auth.dtos;

import queueup.api.dtos.responses.BusinessResponseDto;

public record UserRespondeDto(
        String name,
        String email,
        String role,
        BusinessResponseDto business) {

}
