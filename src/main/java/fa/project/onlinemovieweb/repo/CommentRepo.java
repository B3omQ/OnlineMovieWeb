package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepo extends JpaRepository<Comment, Long> {

    Page<Comment> findByEpisodeIdOrderByCreatedAtDesc(Long episodeId, Pageable pageable);
    Page<Comment> findByMediaIdOrderByCreatedAtDesc(Long mediaId, Pageable pageable);
}
