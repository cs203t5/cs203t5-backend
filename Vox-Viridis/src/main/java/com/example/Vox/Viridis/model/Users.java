package com.example.Vox.Viridis.model;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private Long account_id;
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
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Role> roles;
    private LocalDate dob;

    public UsersDTO convertToDTO() {
        return new UsersDTO(account_id, username, firstName, lastName, email, points, image);
    }
}
