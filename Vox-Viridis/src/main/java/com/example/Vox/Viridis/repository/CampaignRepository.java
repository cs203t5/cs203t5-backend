package com.example.Vox.Viridis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Users;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByTitle(String title);
    List<Campaign> findByCreatedBy(Users createdBy);

    @Query(value = "SELECT c FROM Campaign c WHERE c.title LIKE %:title% AND (COALESCE(:category) IS NULL OR c.category IN (:category)) AND (COALESCE(:location) IS NULL OR c.location IN (:location)) AND (COALESCE(:reward) IS NULL OR (SELECT COUNT(rt) FROM c.rewards r JOIN r.rewardType rt WHERE rt.rewardType IN (:reward))>0) AND c.endDate >= NOW()")
    Page<Campaign> findByTitleAndCategoryAndLocationAndReward(String title, List<String> category, List<String> location, List<String> reward, Pageable pageable);

    //@Query(nativeQuery = true, value = "SELECT created_by FROM campaign WHERE id = :id")
    //Long getCreatedBy(Long id);
}
