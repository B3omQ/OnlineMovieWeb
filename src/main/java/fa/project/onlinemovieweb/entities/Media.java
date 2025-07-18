package fa.project.onlinemovieweb.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "media_genre",
            joinColumns = @JoinColumn(name = "media_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    private int releaseYear;

    private String language;

    private String type; //TV Show or Movie

    private String videoUrl;

    private int views = 0;

    private String poster;

    private String banner;

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL)
    private List<Episode> episodes;

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL)
    private List<WatchHistory> watchHistories;

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public List<WatchHistory> getWatchHistories() {
        return watchHistories;
    }

    public void setWatchHistories(List<WatchHistory> watchHistories) {
        this.watchHistories = watchHistories;
    }
}
