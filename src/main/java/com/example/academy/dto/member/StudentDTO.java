package com.example.academy.dto.member;

import com.example.academy.domain.AvatarImage;
import lombok.Builder;
import lombok.Data;

@Data
public class StudentDTO {

    private String name;
    private String avatarImage;
    private boolean submitted;

    @Builder
    public StudentDTO(String name, String avatarImage) {
        this.name = name;
        this.avatarImage = avatarImage;
    }

}
