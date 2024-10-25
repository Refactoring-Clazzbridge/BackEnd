package com.example.academy.dto.vote;

public class DoVoteDTO {

  private Long voteId;
  private Long voteOptionId;

  public DoVoteDTO(Long voteId, Long voteOptionId) {
    this.voteId = voteId;
    this.voteOptionId = voteOptionId;
  }
}
