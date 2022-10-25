package com.example.Vox.Viridis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Participation")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "noOfStamp must be at least 0")
    private int noOfStamp = 0; // Must be less than or equal to reward.goal (because Reward.goal is the max points that a user can collect)

    @NotNull
    @ManyToOne(optional = false)
    private Reward reward;

    @NotNull
    @ManyToOne(optional = false)
    private Users user;
}
