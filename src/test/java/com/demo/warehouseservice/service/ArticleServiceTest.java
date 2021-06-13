package com.demo.warehouseservice.service;

import com.demo.warehouseservice.dto.ArticleDto;
import com.demo.warehouseservice.model.Article;
import com.demo.warehouseservice.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    private ArticleService articleService;

    private ArticleDto articleDto1;
    private ArticleDto articleDto2;

    @BeforeEach
    public void setUp() {
        this.articleService = new ArticleService(articleRepository);
        this.articleDto1 = ArticleDto.builder().articleId(1L).name("Art 1").stock(5L).build();
        this.articleDto2 = ArticleDto.builder().articleId(2L).name("Art 2").stock(5L).build();
    }

    @Test
    @DisplayName("processArticles :: Empty input should produce empty output")
    public void testProcessArticles_EmptyInput() {
        List<ArticleDto> input = List.of();

        Assertions.assertThat(articleService.processArticles(input)).isEmpty();
    }

    @Test
    @DisplayName("processArticles :: Article1 should be updated")
    public void testProcessArticles_Article_Present() {
        List<ArticleDto> input = List.of(articleDto1, articleDto2);

        Article article1 = Article.builder().id(1L).name("Leg").stock(6L).build();
        Article article2 = Article.builder().id(2L).name("Screw").stock(10L).build();

        // Given
        given(articleRepository.findById(articleDto1.getArticleId())).willReturn(Optional.of(article1));
        given(articleRepository.save(article1)).willReturn(article1);

        // When
        List<Article> articles = articleService.processArticles(input);
        assertThat(articles).isNotEmpty();
    }

}
