package fa.project.onlinemovieweb.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	private String email;

	private String password;

	private Role role; // "USER", "ADMIN"

	private LocalDateTime createdAt = LocalDateTime.now();

	private String gender;// "Male", "Female", "Other"

	private String avatar;

//	@Column(nullable = false)
	private Boolean oauthUser;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<WatchHistory> watchHistories;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<WatchHistory> getWatchHistories() {
		return watchHistories;
	}

	public void setWatchHistories(List<WatchHistory> watchHistories) {
		this.watchHistories = watchHistories;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isOauthUser() {
		return oauthUser;
	}

	public void setOauthUser(Boolean oauthUser) {
		this.oauthUser = oauthUser;
	}


	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Favorite> favorites;

	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}
}
