package com.qcq.second_hand.repository;

import com.qcq.second_hand.entity.categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface categoriesRepository extends JpaRepository<categories,Long> {
}
