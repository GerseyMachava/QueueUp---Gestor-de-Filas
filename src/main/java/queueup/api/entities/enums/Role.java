package queueup.api.entities.enums;

public enum Role {
    ADMIN("admin"),  ATTENDANT("attendant");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
