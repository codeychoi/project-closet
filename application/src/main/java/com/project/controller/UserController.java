package com.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.project.domain.Users;
import com.project.dto.CustomUserDetail;
import com.project.dto.ResponseDTO;
import com.project.dto.UserDTO;
import com.project.security.TokenProvider;
import com.project.service.ItemService;
import com.project.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final ItemService itemService;

    final TokenProvider tokenProvider;

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/me")
    public CustomUserDetail getCurrentUser(){
        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetail) {
            return (CustomUserDetail) authentication.getPrincipal();
        }

        log.info("Authentication object: {}", authentication);
        return null;

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
        try{
            // 요청 데이터를 사용해 User 엔티티 생성
            Users user = Users.builder()
                    .username(userDTO.getUsername())
                    .nickname(userDTO.getNickname())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .email(userDTO.getEmail())
                    .birth(userDTO.getBirth())
                    .build();

            // UserService를 통해 사용자 저장
            Users registeredUser = userService.create(user);

            // 응답용 DTO 생성
            UserDTO responseUserDTO = UserDTO.builder()
                    .username(registeredUser.getUsername())
                    .nickname(registeredUser.getNickname())
                    .id(registeredUser.getId())
                    .email(registeredUser.getEmail())
                    .birth(registeredUser.getBirth())
                    .build();

            // 성공 응답 반환
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            // 에러 응답 반환
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDTO);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        // 자격 증명 확인
        Users user = userService.getByCredentials(
                userDTO.getUsername(),
                userDTO.getPassword(),passwordEncoder);

        if (user != null) {
            // 토큰 생성
            final String token = tokenProvider.create(user);

            // 사용자 정보를 담은 DTO 생성
            final UserDTO responserUserDTO = UserDTO.builder()
                    .id(user.getId())
                    .password(user.getPassword())
                    .createdAt(user.getCreatedAt())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .birth(user.getBirth())
                    .token(token)
                    .build();

            return ResponseEntity.ok().body(responserUserDTO);
        }else {
            // 인증 실패 시 에러 메시지 반환
            ResponseDTO<Object> responseDTO = ResponseDTO.builder()
                    .error("Login failed.")
                    .build();

            return ResponseEntity
                    .badRequest()
                    .body(responseDTO);
        }
    }

    // 아이디 중복 검사
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }

    // 닉네임 중복 검사
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(isAvailable);
    }

    // 이메일 중복 검사
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.ok(isAvailable);
    }
    // 아이디 찾기
    @GetMapping("/find-username")
    public ResponseEntity<ResponseDTO<String>> findUsername(@RequestParam String email) {
        try {
            String username = userService.findUsernameByEmail(email);

            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .status("success")
                    .message("아이디 찾기가 완료되었습니다.")
                    .data(Collections.singletonList(username))
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .status("failure")
                    .message("아이디 찾기 중 오류가 발생했습니다.")
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 비밀번호 재설정 링크 전송
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO<String>> resetPassword(@RequestParam String email, @RequestParam String username) {
        try {
            userService.sendPasswordResetLink(email, username);

            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .status("success")
                    .message("비밀번호 재설정 링크가 이메일로 전송되었습니다.")
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .status("failure")
                    .message("비밀번호 재설정 중 오류가 발생했습니다.")
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }



}