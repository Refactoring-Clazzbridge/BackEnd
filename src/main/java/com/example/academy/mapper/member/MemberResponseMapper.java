package com.example.academy.mapper.member;

import com.example.academy.domain.Member;
import com.example.academy.dto.auth.AuthResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MemberResponseMapper {

    public static AuthResponseDTO toDTO(Member member) {
        return AuthResponseDTO.builder()
            .id(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .memberType(member.getMemberType().getType())
            .gitUrl(member.getGitUrl())
            .profileImageUrl(member.getAvatarImage().getAvatarImageUrl())
            .build();
    }

}
