package queeup.api.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import queeup.api.entities.base.BaseEntity;
import queeup.api.entities.enums.BusinessType;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "businesses")
@SuperBuilder
public class Business extends BaseEntity {

    @Column(nullable = false)
    private String name;
    private String address;
    @Column(nullable = false, unique = true)
    private String contact;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BusinessType type;

    @OneToMany(mappedBy = "business")
    private List<User> users;
    @OneToMany(mappedBy = "business")
    private List<Category> categories;
}
