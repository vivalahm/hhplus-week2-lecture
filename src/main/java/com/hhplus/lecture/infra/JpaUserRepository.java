package com.hhplus.lecture.infra;

import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.business.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    @Override
    default User getUserById(Long userId) {
        return findById(userId).orElse(null);
    }

    @Override
    default User saveUser(User user) {
        return save(user);
    }
}
