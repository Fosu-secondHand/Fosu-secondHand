package com.qcq.second_hand.repository;


import com.qcq.second_hand.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesRepository extends JpaRepository<Messages,Long> {
}
