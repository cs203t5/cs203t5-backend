package com.example.Vox.Viridis.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Reward")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @JsonProperty("rewardName")
    private String rewardName;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "offered_by")
    private Campaign offeredBy;

    @JsonIgnore
    @NotNull
    @ManyToOne
    @JoinColumn(name = "reward_type_id")
    private RewardType rewardType;

    @Min(value = 1, message = "goal must be at least 1")
    @NotNull(message = "goal cannot be null")
    private int goal; // This is used to store the max points that a person can save

    @NotNull(message = "tnc cannot be null")
    @Size(min = 5, message = "tnc must be at least 5 letters")
    private String tnc; // terms and conditions

    @JsonProperty("rewardType")
    public String rewardType() {
        return rewardType.getRewardType();
    }

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_reward", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "reward_id"))
    private List<Users> users;
}
