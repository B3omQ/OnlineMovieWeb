package fa.project.onlinemovieweb.entities;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., Action, Drama, Comedy

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    private List<Media> mediaList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
    }
}

