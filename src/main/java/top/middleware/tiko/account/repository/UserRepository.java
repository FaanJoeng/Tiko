package top.middleware.tiko.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.middleware.tiko.account.dto.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
