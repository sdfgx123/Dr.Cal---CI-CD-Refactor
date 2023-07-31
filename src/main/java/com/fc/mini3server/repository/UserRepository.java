package com.fc.mini3server.repository;

import com.fc.mini3server.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByOrderByIdDesc(Pageable pageable);
}
