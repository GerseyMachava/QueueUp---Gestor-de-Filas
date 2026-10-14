package queueup.api.auth.dtos;

import queueup.api.entities.enums.Role;

public record RegisterRequestDto(String name, String email, String password, Role role, Long businessId) {

}
