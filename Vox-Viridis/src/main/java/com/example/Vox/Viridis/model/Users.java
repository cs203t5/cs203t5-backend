package com.example.Vox.Viridis.model;

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
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private int points = 0;
    private String image;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Role> roles;

}
