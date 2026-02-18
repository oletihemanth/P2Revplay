package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistRequest {
    private String name;
    private String description;
    private String privacy;
}
