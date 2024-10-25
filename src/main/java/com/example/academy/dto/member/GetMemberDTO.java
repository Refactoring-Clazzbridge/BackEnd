package com.example.academy.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMemberDTO {

  private Long id;
  private String memberId;
  private String name;
  private String email;
  private String phone;
  private String memberType;
  private String courseTitle;

  public GetMemberDTO() {
  }

  public GetMemberDTO(Long id, String memberId,String name, String email,
      String phone, String memberType, String courseTitle) {
    this.id = id;
    this.memberId = memberId;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.memberType = memberType;
    this.courseTitle = courseTitle;
  }
}