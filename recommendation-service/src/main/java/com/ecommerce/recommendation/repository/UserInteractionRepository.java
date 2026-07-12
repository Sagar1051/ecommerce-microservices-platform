package com.ecommerce.recommendation.repository;

import com.ecommerce.recommendation.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    // Step 1: what has this user already interacted with?
    @Query("SELECT DISTINCT ui.productId FROM UserInteraction ui WHERE ui.userId = :userId")
    List<Long> findDistinctProductIdsByUserId(@Param("userId") Long userId);

    // Step 2: who else interacted with any of those same products?
    // ("people who liked X also liked Y" starts by finding the "people who liked X" part)
    @Query("SELECT DISTINCT ui.userId FROM UserInteraction ui " +
            "WHERE ui.productId IN :productIds AND ui.userId <> :excludeUserId")
    List<Long> findSimilarUserIds(@Param("productIds") List<Long> productIds,
                                    @Param("excludeUserId") Long excludeUserId);

    // Step 3: what did those similar users interact with? (filtered/scored in Java —
    // simpler to read than one giant SQL query, and this table stays small enough
    // for a portfolio project that it doesn't matter for performance)
    List<UserInteraction> findByUserIdIn(List<Long> userIds);

    // Cold-start fallback: if a user has no history yet, just show what's
    // trending across everyone.
    @Query("SELECT ui.productId FROM UserInteraction ui GROUP BY ui.productId " +
            "ORDER BY COUNT(ui) DESC")
    List<Long> findTrendingProductIds();
}
