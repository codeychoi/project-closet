package com.project.repository.admin;

import com.project.domain.User;
import com.project.dto.AdminUserDTO;
import com.project.dto.PageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface AdminUserRepository extends JpaRepository<User, Long> {
    @Query("SELECT new com.project.dto.AdminUserDTO( " +
            "u.id,u.email, u.nickname, g.grade, u.birth, " +
            "(SELECT COALESCE(SUM(p.point), 0) FROM Point p WHERE p.user.id = u.id), " +
            "(SELECT COUNT(o) FROM OrderList o WHERE o.user.id = u.id), " +
            "u.status, u.createdAt) " +
            "FROM User u " +
            "LEFT JOIN u.grade g " +
            "WHERE ((:searchKeyword IS NULL OR :searchInput ='') OR " +
            " (:searchKeyword ='username' AND u.username LIKE CONCAT('%',:searchInput, '%')) OR"+
            " (:searchKeyword ='nickname' AND u.nickname LIKE CONCAT('%',:searchInput,'%')) OR" +
            " (:searchKeyword ='email' AND u.email LIKE CONCAT('%',:searchInput,'%')))" +
            "AND (:grade ='' OR g.grade = :grade)" +
            "AND (:startDate IS NULL OR u.createdAt >= :startDate)"+
            "AND (:endDate IS NULL OR u.createdAt <= :endDate)")
    Page<AdminUserDTO> findAllAdminUsers(Pageable pageable,
                                         @Param("searchKeyword") String searchKeyword,
                                         @Param("searchInput") String searchInput,
                                         @Param("startDate") Timestamp startDate,
                                         @Param("endDate") Timestamp endDate,
                                         @Param("grade") String grade);
}
