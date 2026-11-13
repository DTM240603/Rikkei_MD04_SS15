package ra.edu.hw.md04.ss15.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.edu.hw.md04.ss15.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
