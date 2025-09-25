package com.qcq.second_hand.repository;

import com.qcq.second_hand.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface usersRepository extends JpaRepository<Users,Long> {

    Users findByUserId(Long userId);
}
