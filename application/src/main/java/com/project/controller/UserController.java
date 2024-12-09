package com.project.controller;


import com.project.domain.Users;
import com.project.dto.ItemDetailItemDTO;
import com.project.security.TokenProvider;
import com.project.service.ItemService;
import com.project.service.UsersService;
import com.project.dto.ResponseDTO;
import com.project.dto.UserDTO;
import com.project.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    final UsersService userService;
    private final ItemService itemService;

    final TokenProvider tokenProvider;

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
                    .createdAt(user.getCreated_at())
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

}