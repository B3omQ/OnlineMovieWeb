package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Comment;
import fa.project.onlinemovieweb.entities.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface CommentRepo extends JpaRepository<Comment, Long> {

    Page<Comment> findByEpisodeIdOrderByCreatedAtDesc(Long episodeId, Pageable pageable);
    Page<Comment> findByMediaIdOrderByCreatedAtDesc(Long mediaId, Pageable pageable);

    Page<Comment> findByMediaIdAndParentIsNullOrderByCreatedAtDesc(Long mediaId, Pageable pageable);
    List<Comment> findByParent(Comment parent);
    Page<Comment> findByEpisodeIdAndParentIsNullOrderByCreatedAtDesc(Long episodeId, Pageable pageable);

    @Query("select c from Comment c where c.parent.user.id = :userId")
    List<Comment> findByParentUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comment_likes WHERE user_id = :userId", nativeQuery = true)
    void clearLikesForUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "update Comment c set c.taggedUser = null WHERE c.taggedUser.id = :userId")
    void clearTaggedUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "delete from Comment c WHERE c.user.id = :userId")
    void clearUserFromComments(@Param("userId") Long userId);
}
