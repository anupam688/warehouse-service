package com.demo.warehouseservice.service;

import com.demo.warehouseservice.repository.ArticleRepository;
import com.demo.warehouseservice.dto.ArticleDto;
import com.demo.warehouseservice.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Article service.
 */
@Service
public class ArticleService {

    private final ArticleRepository articleDao;

    @Autowired
    public ArticleService(ArticleRepository articleDao) {
        this.articleDao = articleDao;
    }

    public Article processArticle(final ArticleDto articleDto) {
        Article article = articleDao.findById(articleDto.getArticleId())
                .orElse(Article.builder().id(articleDto.getArticleId()).build());
        article.setName(articleDto.getName());
        article.setStock(articleDto.getStock());
        return articleDao.save(article);
    }

    /**
     * Process articles list.
     *
     * @param articleDtos the article dtos
     * @return the list
     */
    public List<Article> processArticles(final List<ArticleDto> articleDtos) {
        return articleDtos.stream()
                .map(this::processArticle)
                .collect(Collectors.toList());
    }

    /**
     * Gets all articles.
     *
     * @return the all articles
     */
    public List<Article> getAllArticles() {
        return articleDao.findAll();
    }

}
