package com.example.matching.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 30)
    private String displayName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 10)
    private String gender; // "male" / "female" / "other" のどれかを入れる

    @Column(length = 500)
    private String bio; // 自己紹介文(空でもOKなので nullable 指定なし)

    @Column(length = 20)
    private String prefecture; // 都道府県

    // ---- getter / setter ----
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPrefecture() { return prefecture; }
    public void setPrefecture(String prefecture) { this.prefecture = prefecture; }
}
