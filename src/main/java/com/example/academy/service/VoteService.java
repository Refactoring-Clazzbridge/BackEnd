package com.example.academy.service;

import com.example.academy.domain.Course;
import com.example.academy.domain.Member;
import com.example.academy.domain.Vote;
import com.example.academy.domain.VoteOption;
import com.example.academy.dto.member.CustomUserDetails;
import com.example.academy.dto.vote.AddVoteDTO;
import com.example.academy.dto.vote.DoVoteDTO;
import com.example.academy.dto.vote.GetAllVoteDTO;
import com.example.academy.dto.vote.GetVoteDTO;
import com.example.academy.enums.MemberRole;
import com.example.academy.exception.common.NotFoundException;
import com.example.academy.exception.post.PostBadRequestException;
import com.example.academy.repository.mysql.CourseRepository;
import com.example.academy.repository.mysql.MemberRepository;
import com.example.academy.repository.mysql.VoteOptionRepository;
import com.example.academy.repository.mysql.VoteRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

  private final VoteRepository voteRepository;
  private final CourseRepository courseRepository;
  private final MemberRepository memberRepository;
  private final VoteOptionRepository voteOptionRepository;

  // 로그인된 유저 정보에 접근할 수 있는 서비스
  private final AuthService authService;

  public VoteService(VoteRepository voteRepository, CourseRepository courseRepository,
      AuthService authService, MemberRepository memberRepository,
      VoteOptionRepository voteOptionRepository) {
    this.voteRepository = voteRepository;
    this.courseRepository = courseRepository;
    this.authService = authService;
    this.memberRepository = memberRepository;
    this.voteOptionRepository = voteOptionRepository;
  }

  public void addVote(AddVoteDTO addVoteDTO) {
    CustomUserDetails user = authService.getAuthenticatedUser();

    Member member = memberRepository.findById(user.getUserId())
        .orElseThrow(PostBadRequestException::new);

    if (!member.getMemberType().getType().equalsIgnoreCase(MemberRole.ROLE_TEACHER.toString())) {
      throw new PostBadRequestException("강사만 투표를 추가할 수 있습니다.");
    }

    // 강사 강의 중복 방지
    Course course;
    List<Course> courses = courseRepository.findByInstructor(member);
    if (courses.isEmpty()) {
      throw new NotFoundException("담당 과정이 존재하지 않습니다.");
    } else if (courses.size() != 1) {
      throw new PostBadRequestException("강사가 2개 이상의 강의를 담당하고 있습니다.");
    } else {
      course = courses.get(0);
    }

    // Vote 객체 생성
    Vote vote = new Vote();
    vote.setCourse(course);
    vote.setTitle(addVoteDTO.getTitle());
    vote.setDescription(addVoteDTO.getDescription());
    vote.setStartDate(addVoteDTO.getStartDate());
    vote.setEndDate(addVoteDTO.getEndDate());
    vote.setIsExpired(false);

    // Vote_option 객체 생성
    List<VoteOption> voteOptions = new ArrayList<>();
    for (String optionText : addVoteDTO.getOptionText()) { // 수정: List<String>을 사용
      VoteOption option = new VoteOption();
      option.setOptionText(optionText);
      option.setVote(vote);

      voteOptions.add(option);
    }

    voteRepository.save(vote);
    voteOptionRepository.saveAll(voteOptions);
  }

  public void doVote(DoVoteDTO doVoteDTO) {

  }

  public List<GetAllVoteDTO> getAllVote() {
    LocalDateTime now = LocalDateTime.now();
    List<Vote> votes = voteRepository.findAllWithCourseAndInstructor();

    // 상태 업데이트
    for (Vote vote : votes) {
      boolean isExpired = now.isAfter(vote.getEndDate()) || now.isBefore(vote.getStartDate());
      vote.setIsExpired(isExpired);
      voteRepository.save(vote);
    }

    List<GetAllVoteDTO> getAllVoteDTOS = new ArrayList<>();
    for (Vote vote : votes) {
      getAllVoteDTOS.add(new GetAllVoteDTO(vote.getId(), vote.getCourse().getTitle()
          , vote.getTitle(), vote.getDescription()
          , vote.getStartDate(), vote.getEndDate(), vote.getIsExpired()));
    }
    //투표 시작날짜가 현재보다 빠르고 종료날짜가 느린 값들은 Ture 아니면 false;
    return getAllVoteDTOS;
  }

  public GetVoteDTO getVote(Long id) {
    Optional<Vote> votes = voteRepository.findById(id);
    Vote vote = votes.get();
    List<VoteOption> voteOption = voteOptionRepository.findByVote(vote);
    List<String> options = new ArrayList<>();
    for (VoteOption option : voteOption) {
      options.add(option.getOptionText());
    }

    GetVoteDTO getVoteDTO = new GetVoteDTO(vote.getId(), vote.getCourse().getTitle(),
        vote.getTitle(), vote.getDescription(), vote.getStartDate(),
        vote.getEndDate(), vote.getIsExpired(), options);

    return getVoteDTO;
  }


  public void deleteVote(Long id) throws Exception {
    try {
      voteRepository.deleteById(id);
    } catch (Exception e) {
      throw new Exception("잘못된 삭제입니다.");
    }
  }
}
