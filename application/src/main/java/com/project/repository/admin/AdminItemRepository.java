package com.project.repository.admin;

import com.project.domain.detail.ItemDetail;
import com.project.dto.AdminItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface AdminItemRepository extends JpaRepository<ItemDetail, Long> {

    @Query("SELECT new com.project.dto.AdminItemDTO(" +
            "id.id, id.main_image,id.item_name,id.item_category,id.item_price,id.status,id.created_at)" +
            "FROM ItemDetail id " +
            "WHERE ((:searchKeyword IS NULL OR :searchInput ='') OR " +
            " (:searchKeyword ='itemName' AND id.item_name LIKE CONCAT('%',:searchInput,'%'))) " +
            "AND (:category ='' OR id.item_category = :category)" +
            "AND (:startDate IS NULL OR id.created_at >= :startDate)"+
            "AND (:endDate IS NULL OR id.created_at <= :endDate)" +
            "AND (:minPrice = 0 OR id.item_price >= :minPrice)" +
            "AND (:maxPrice = 0 OR id.item_price <= :maxPrice)" +
            "AND (:status ='' OR id.status = :status )")
    Page<AdminItemDTO> findItem(Pageable pageable,
                                @Param("searchKeyword")String searchKeyword,
                                @Param("searchInput")String searchInput,
                                @Param("startDate")Timestamp startDate,
                                @Param("endDate")Timestamp endDate,
                                @Param("minPrice")Integer minPrice,
                                @Param("maxPrice")Integer maxPrice,
                                @Param("category")String category,
                                @Param("status")String status);
}
