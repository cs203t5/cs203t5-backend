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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.example.Vox.Viridis.model.dto.RoleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Role")
public class Role {
    @Id
    @SequenceGenerator(name = "roles_seq", sequenceName = "roles_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq")
    private Long roleId;
    @Column(unique = true)
    @NotNull(message = "Role name is required!")
    @NotBlank(message = "Role name is required!")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    private List<Users> user;

    public RoleDTO convertToDTO() {
        return new RoleDTO(roleId, name);
    }
}


