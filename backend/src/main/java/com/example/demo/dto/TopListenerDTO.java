package com.example.demo.dto;

public class TopListenerDTO {
    private String userName;
    private String profilePictureUrl;
    private Long totalPlays;

    public TopListenerDTO(String userName, String profilePictureUrl, Long totalPlays) {
        this.userName = userName;
        this.profilePictureUrl = profilePictureUrl;
        this.totalPlays = totalPlays;
    }

    public String getUserName() { return userName; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public Long getTotalPlays() { return totalPlays; }
}