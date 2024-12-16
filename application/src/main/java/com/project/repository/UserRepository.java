package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.domain.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
  Users findByUsername(String username); // 사용자 이름으로 사용자 찾기
  Boolean existsByUsername(String username); // 사용자 이름 중복 여부 확인
  Boolean existsByNickname(String nickname); // 닉네임 중복 여부 확인
  Boolean existsByEmail(String email); // 이메일 중복 여부 확인

  Users findByUsernameAndPassword(String username, String password); // 사용자 이름과 비밀번호로 사용자 찾기


  Users findByNaverId(String providerId);
  Users findByKakaoId(String providerId);
  Users findByNickname(String nickname);
}



