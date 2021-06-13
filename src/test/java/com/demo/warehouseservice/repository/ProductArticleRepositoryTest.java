package com.demo.warehouseservice.repository;

import com.demo.warehouseservice.model.Article;
import com.demo.warehouseservice.model.Product;
import com.demo.warehouseservice.model.ProductArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DataJpaTest
public class ProductArticleRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ProductRepository productRepository;
    private final ProductArticleRepository productArticleRepository;

    private Article article1;
    private Article article2;

    private Product product1;

    private ProductArticle productArticle1;
    private ProductArticle productArticle2;


    @Autowired
    public ProductArticleRepositoryTest(ArticleRepository articleRepository,
                                        ProductRepository productRepository,
                                        ProductArticleRepository productArticleRepository) {
        this.articleRepository = articleRepository;
        this.productRepository = productRepository;
        this.productArticleRepository = productArticleRepository;
    }

    @BeforeEach
    public void setUp() {
        this.article1 = articleRepository.save(Article.builder().id(1L).name("Art 1").stock(22L).build());
        this.article2 = articleRepository.save(Article.builder().id(2L).name("Art 2").stock(3L).build());

        this.product1 = productRepository.save(Product.builder().name("Product 1").build());

        this.productArticle1 = ProductArticle.builder().product(product1).article(article1).quantity(2L).build();
        this.productArticle2 = ProductArticle.builder().product(product1).article(article2).quantity(1L).build();
    }

    @Test
    public void testFindAllByProduct_RecordsFound() {
        // Given
        ProductArticle productArticle1 = productArticleRepository.save(this.productArticle1);
        ProductArticle productArticle2 = productArticleRepository.save(this.productArticle2);
        List<ProductArticle> expected = List.of(productArticle1, productArticle2);

        // When
        List<ProductArticle> actual = productArticleRepository.findAllByProduct(product1);

        // Then
        assertIterableEquals(expected, actual);
    }

    @Test
    public void testFindAllNyProduct_NotFound() {
        // Given
        Product product = Product.builder().id(2L).name("Test product").build();

        // When
        List<ProductArticle> actual = productArticleRepository.findAllByProduct(product);

        // Then
        assertThat(actual).isEmpty();
    }

}
