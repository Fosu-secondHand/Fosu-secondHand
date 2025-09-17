package com.qcq.second_hand.repository;

import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.entity.favorite;
import com.qcq.second_hand.entity.products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface favoriteRepository extends JpaRepository<favorite,Long> {

    boolean existsByUserAndProduct(Users user, products product);


    favorite findByUserAndProduct(Users user, products product);

}
