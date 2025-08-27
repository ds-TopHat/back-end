package com.mathfusion.domain.user.repository;

import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.entity.enums.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialUserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndLoginType(String socialId, LoginType loginType);
}
