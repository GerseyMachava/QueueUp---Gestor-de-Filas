package queueup.api.entities.enums;

public enum Role {
    ADMIN("admin"),  ATTENDANT("attendant"), SUPER_ADMIN("super_admin");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
