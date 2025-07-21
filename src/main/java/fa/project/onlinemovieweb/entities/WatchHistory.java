package fa.project.onlinemovieweb.entities;

import jakarta.persistence.*;
        import java.time.LocalDateTime;

@Entity
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime watchedAt = LocalDateTime.now();

    private int durationWatched;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "media_id", nullable = true)
    private Media media;

    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = true)
    private Episode episode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(LocalDateTime watchedAt) {
        this.watchedAt = watchedAt;
    }

    public int getDurationWatched() {
        return durationWatched;
    }

    public void setDurationWatched(int durationWatched) {
        this.durationWatched = durationWatched;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }
}
