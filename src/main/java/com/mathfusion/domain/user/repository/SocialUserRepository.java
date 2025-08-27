package com.mathfusion.domain.user.repository;

import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.entity.enums.LoginType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialUserRepository {
    Optional<User> findBySocialIdAndLoginType(String socialId, LoginType loginType);
}
