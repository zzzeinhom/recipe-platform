package com.recipesharing.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class RatingResponse {
   private Long id;
    private Integer rating;
    private String review;
    private String username;
    private LocalDateTime createdAt;
}
