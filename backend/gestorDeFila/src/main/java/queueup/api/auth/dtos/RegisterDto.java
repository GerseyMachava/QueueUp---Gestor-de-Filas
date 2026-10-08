package queueup.api.auth.dtos;

import queueup.api.entities.enums.Role;

public record RegisterDto(String name, String email, String password, Role role) {

}
