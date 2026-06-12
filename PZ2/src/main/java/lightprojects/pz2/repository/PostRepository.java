package lightprojects.pz2.repository;

import lightprojects.pz2.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();

    boolean existsByCategoryId(Long categoryId);
}
