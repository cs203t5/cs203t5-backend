package com.example.Vox.Viridis.model.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaginationDTO <T> {
    private List<T> elements;
    private long totalElements;
    private int totalNumPage;

    public PaginationDTO(Page<T> page) {
        this.elements = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalNumPage = page.getTotalPages();
    }
}
