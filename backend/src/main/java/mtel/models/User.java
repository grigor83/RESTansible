package mtel.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
    @Column(name = "name")
    private String name;

    @NotBlank
    @Basic
    @Column(name = "lastname")
    private String lastname;

    @NotBlank
    @Basic
    @Column(name = "username")
    private String username;

    @NotBlank
    @Basic
    @Column(name = "password")
    private String password;

    @NotNull
    @Email
    @Basic
    @Column(name = "email")
    private String email;

    @ValidPhoneNumber
    @Basic
    @Column(name = "phone")
    private String phoneNumber;

}
