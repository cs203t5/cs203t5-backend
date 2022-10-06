package com.example.Vox.Viridis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Vox.Viridis.model.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByTitle(String title);

    @Query(nativeQuery = true, 
        value = "SELECT * FROM campaign WHERE title LIKE %?1% AND (IFNULL(?2,'')='' OR category = ?2) AND (IFNULL(?3,'')='' OR location=?3) AND end_date >= NOW() ORDER BY ?#{#pageable}", 
        countQuery = "SELECT COUNT(*) FROM campaign WHERE title LIKE %?1% AND (IFNULL(?2,'')='' OR category=?2) AND (IFNULL(?3,'')='' OR location=?3) AND end_date >= NOW()")
    Page<Campaign> findByTitleAndCategoryAndLocation(String title, String category, String location, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT created_by FROM campaign WHERE id = :id")
    String getCreatedBy(Long id);
}
