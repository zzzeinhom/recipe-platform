package com.recipesharing.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RatingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addRating_requiresAuth() throws Exception {

        mockMvc.perform(
                        post("/api/recipes/1/rating")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            { "score": 5, "comment": "Nice" }
                        """)
                )
                .andExpect(status().isUnauthorized());
    }
}
