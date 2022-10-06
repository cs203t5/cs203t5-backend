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

import org.springframework.format.annotation.DateTimeFormat;

import com.example.Vox.Viridis.model.validation.ConsistentDate;
import com.example.Vox.Viridis.model.validation.FutureOrToday;
import com.example.Vox.Viridis.model.validation.Location;

@ConsistentDate
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Campaign")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "title")
    @NotNull(message = "Campaign's title should not be null")
    @Size(min = 5, max = 255, message = "Campaign's title should be at least 5 characters long")
    private String title;

    private String description;

    @NotNull(message = "Campaign's startDate should not be null")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @FutureOrToday
    private LocalDateTime startDate;

    @NotNull(message = "Campaign's endDate should not be null")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @FutureOrToday
    @Column(name="end_date")
    private LocalDateTime endDate; // should store 2359 if intending for whole day
    
    @Column(name="location")
    @Location
    private String location; // North, South, East, West, Central
    private String address;
    private char status;
    private String image;
    @Column(name="category")
    private String category;
    private int goal;
    @Column(name="created_by")
    private String createdBy; // Users.username
    @Column(name="created_on")
    private LocalDateTime createdOn = LocalDateTime.now();
}
