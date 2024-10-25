package com.example.academy.repository;

import com.example.academy.domain.Seat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findBySeatNumber(String seatNumber);

    Optional<Seat> findByMember_Id(Long memberId);
}