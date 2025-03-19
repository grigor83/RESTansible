package mtel.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @NotBlank
    @Basic
    @Column(name = "username")
    private String username;

    @NotBlank
    @Basic
    @Column(name = "password")
    private String password;

    @NotBlank
    @Basic
    @Column(name = "phone")
    private String phone;

    @NotBlank
    @Basic
    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy ="user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Inventory> inventories;

    @OneToMany(mappedBy ="user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Playbook> playbooks;

}
