package com.recipesharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRecipes_public() throws Exception {

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk());
    }

    @Test
    void createRecipe_unauthorized() throws Exception {

        mockMvc.perform(
                        post("/api/recipes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isUnauthorized());
    }
}
