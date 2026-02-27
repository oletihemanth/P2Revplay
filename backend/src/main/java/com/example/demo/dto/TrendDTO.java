package com.example.demo.dto;

public class TrendDTO {
    private String date;
    private Long playCount;

    public TrendDTO(String date, Long playCount) {
        this.date = date;
        this.playCount = playCount;
    }

    public String getDate() { return date; }
    public Long getPlayCount() { return playCount; }
}