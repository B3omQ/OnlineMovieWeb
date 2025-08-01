package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Comment;
import fa.project.onlinemovieweb.entities.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepo extends JpaRepository<Comment, Long> {

    Page<Comment> findByEpisodeIdOrderByCreatedAtDesc(Long episodeId, Pageable pageable);
    Page<Comment> findByMediaIdOrderByCreatedAtDesc(Long mediaId, Pageable pageable);

    Page<Comment> findByMediaIdAndParentIsNullOrderByCreatedAtDesc(Long mediaId, Pageable pageable);
    List<Comment> findByParent(Comment parent);
}
