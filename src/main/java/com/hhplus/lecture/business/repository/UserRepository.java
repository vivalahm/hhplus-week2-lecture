package com.hhplus.lecture.business.repository;

import com.hhplus.lecture.business.entity.User;

public interface UserRepository {
    User getUserById(Long userId);

    User saveUser(User user);
}
