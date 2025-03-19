package mtel.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @NotBlank
    @Basic
    @Column(name = "filename")
    private String filename;

    @NotBlank
    @Basic
    @Column(name = "filepath")
    private String filepath;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

}
