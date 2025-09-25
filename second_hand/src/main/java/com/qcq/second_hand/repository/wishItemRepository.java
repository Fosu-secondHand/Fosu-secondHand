package com.qcq.second_hand.repository;

import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.entity.products;
import com.qcq.second_hand.entity.wishItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface wishItemRepository extends JpaRepository<wishItem,Long> {

    public boolean existsByUserAndProduct(Users user, products product);

    wishItem findByUserAndProduct(Users user, products product);
}
