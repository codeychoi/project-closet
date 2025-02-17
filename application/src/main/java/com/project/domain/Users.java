package com.project.domain;

import java.util.List;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@Data
@Table(name = "users")
@ToString(exclude = {"point", "orderList", "password", "grade"}) // 로그 출력 시 순환참조 문제 방지
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디
    
    private String username; // 계정

    private String password; // 비밀번호

    private String nickname; // 네이버 로그인 사용자는 naver_id 사용

    private String email; // 이메일

    private String naverId; // 네이버

    private String kakaoId; // 카카오

    private String birth; // 생년월일

    private String profileImage; // 프로필 사진

    private String name; // 이름

    private String phone; // 휴대폰번호

    private String introduction; // 자기소개

    private String style; // 스타일

    private String status; // 상태

    private Integer height; // 키

    private Integer weight; // 몸무게

    private String size; // 사이즈

    private Boolean isReleased; // 공개여부

    @CreationTimestamp
    private Timestamp createdAt; // 가입일자

    private Timestamp inactiveDate; // 비활성화일자

    private Timestamp reactiveDate; // 차단해제일자

    private Timestamp deletedAt; // 탈퇴일자

    private String role; // 역할

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id" ,referencedColumnName = "id", nullable = false)
    private Grade grade; // 등급

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Point> point;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrderHistory> orderList;

}
