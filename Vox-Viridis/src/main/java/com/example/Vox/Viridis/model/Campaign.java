package com.example.Vox.Viridis.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Campaign")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "Campaign's title should not be null")
    @Size(min = 5, max = 255, message = "Campaign's title should be at least 5 characters long")
    private String title;

    private String description;

    @NotNull(message = "Campaign's startTime should not be null")
    private LocalDateTime startTime;

    @NotNull(message = "Campaign's endTime should not be null")
    private LocalDateTime endTime;
    
    private String location;
    private char status;
    private String image;
    private String category;
    private int goal;
}
