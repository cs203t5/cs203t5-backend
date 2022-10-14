package com.example.Vox.Viridis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Vox.Viridis.model.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByTitle(String title);

    @Query(value = "SELECT c FROM Campaign c WHERE c.title LIKE %:title% AND (COALESCE(:category) IS NULL OR c.category IN :category) AND (COALESCE(:location) IS NULL OR c.location IN :location) AND c.endDate >= NOW()")
    Page<Campaign> findByTitleAndCategoryAndLocation(String title, List<String> category, List<String> location, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT created_by FROM campaign WHERE id = :id")
    Long getCreatedBy(Long id);
}
