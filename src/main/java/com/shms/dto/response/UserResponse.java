package com.shms.dto.response;

import java.time.LocalDateTime;

import com.shms.entity.enums.Role;
import com.shms.entity.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Role role;

    private UserStatus status;
}