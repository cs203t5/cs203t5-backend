package com.example.Vox.Viridis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Vox.Viridis.model.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByTitle(String title);

    @Query(nativeQuery = true, value = "SELECT * FROM campaign WHERE title LIKE %:title% ORDER BY title LIMIT 20")
    List<Campaign> findTop20ByTitleOrderByTitleAsc(String title);

    @Query(nativeQuery = true, value = "SELECT createdBy FROM campaign WHERE id = :id")
    String getCreatedBy(Long id);
}
