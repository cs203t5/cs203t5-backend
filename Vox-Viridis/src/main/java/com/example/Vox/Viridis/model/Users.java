package com.example.Vox.Viridis.model;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.example.Vox.Viridis.model.dto.UsersDTO;
import com.example.Vox.Viridis.model.validation.Password;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users")
public class Users {
    @Id
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    private Long accountId;
    @Column(unique = true)
    @NotNull(message = "Username is required!")
    @NotBlank(message = "Username is required!")
    private String username;
    @Password
    private String password;
    @NotNull(message = "First name is required!")
    @NotBlank(message = "First name is required!")
    private String firstName;
    @NotNull(message = "Last name is required!")
    @NotBlank(message = "Last name is required!")
    private String lastName;
    @Column(unique = true)
    @NotNull(message = "Email is required!")
    @NotBlank(message = "Email is required!")
    @Email
    private String email;
    private int points = 0;
    private String image;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role roles;
    private LocalDate dob;
    private boolean enabled;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Participation> userParticipation; // for customer

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy")
    private List<Products> userProducts;

    public UsersDTO convertToDTO() {
        return new UsersDTO(accountId, username, firstName, lastName, email, points, image,
                roles.getName());
    }
}
