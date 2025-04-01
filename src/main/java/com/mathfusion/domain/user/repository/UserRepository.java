package com.mathfusion.domain.user.repository;

import com.mathfusion.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); //이메일로 사용자 정보가져온다.
}
