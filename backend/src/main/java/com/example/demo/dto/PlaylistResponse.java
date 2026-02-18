package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {

    private Long playlistId;
    private String name;
    private String description;
    private String privacy;
    private String coverImageUrl;
}
