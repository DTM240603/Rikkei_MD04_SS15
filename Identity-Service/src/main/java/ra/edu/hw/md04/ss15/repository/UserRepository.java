package ra.edu.hw.md04.ss15.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ra.edu.hw.md04.ss15.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
