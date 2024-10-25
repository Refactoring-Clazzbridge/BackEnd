package com.example.academy.dto.member;


import com.example.academy.domain.MemberType;
import com.example.academy.domain.AvatarImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {

  private String memberId;
  private String name;
  private String email;
  private String phone;
  private MemberType memberType;
  private AvatarImage avatarImage;
  private String gitUrl;
  private String bio;

  public MemberDTO(String memberId, String name, String email, String phone, MemberType memberType,
      AvatarImage avatarImage, String gitUrl, String bio) {
    this.memberId = memberId;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.memberType = memberType;
    this.avatarImage = avatarImage;
    this.gitUrl = gitUrl;
    this.bio = bio;

  }
}