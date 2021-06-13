package com.demo.warehouseservice.controller;

import com.demo.warehouseservice.jobs.FileProcessor;
import com.demo.warehouseservice.model.Article;
import com.demo.warehouseservice.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {

    public static final String ARTICLES_URL = "/articles";
    public static final String TEST_RESOURCE_PATH = "classpath:test_resource/articles/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private FileProcessor fileProcessor;

    @Test
    public void test_POST_importArticles_Success() throws Exception {
        // Given
        Article article1 = Article.builder().id(1L).name("Art 1").stock(20L).build();
        Article article2 = Article.builder().id(2L).name("Art 2").stock(10L).build();
        given(articleService.processArticles(anyList())).willReturn(List.of(article1, article2));


        File file = ResourceUtils.getFile(TEST_RESOURCE_PATH + "TEST_article_inventory_success.json");

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart(ARTICLES_URL)
                        .file("file",Files.readAllBytes(file.toPath()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_POST_importArticles_BadRequest() throws Exception {
        File file = ResourceUtils.getFile(TEST_RESOURCE_PATH + "TEST_article_inventory_bad_request.json");

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart(ARTICLES_URL)
                        .file("file",Files.readAllBytes(file.toPath()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
