package com.demo.warehouseservice.service;

import com.demo.warehouseservice.dto.ProductArticleDto;
import com.demo.warehouseservice.dto.ProductDto;
import com.demo.warehouseservice.exception.ValidationException;
import com.demo.warehouseservice.model.Article;
import com.demo.warehouseservice.model.Product;
import com.demo.warehouseservice.model.ProductArticle;
import com.demo.warehouseservice.repository.ArticleRepository;
import com.demo.warehouseservice.repository.ProductArticleRepository;
import com.demo.warehouseservice.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductArticleRepository productArticleRepository;

    private ProductService productService;


    private ProductDto productDto1;
    private ProductDto productDto2;
    private ProductArticleDto productArticleDto1;
    private ProductArticleDto productArticleDto2;

    private Product product1;
    private Product product2;

    private Article article1;
    private Article article2;

    private ProductArticle productArticle1;
    private ProductArticle productArticle2;
    private ProductArticle productArticle3;

    @BeforeEach
    public void setUp() {
        this.productService = new ProductService(articleRepository, productRepository, productArticleRepository);

        this.productArticleDto1 = ProductArticleDto.builder().articleId(1L).quantity(5L).build();
        this.productArticleDto2 = ProductArticleDto.builder().articleId(2L).quantity(10L).build();

        this.productDto1 = ProductDto.builder().name("Product 1").price(BigDecimal.ZERO)
                .productArticles(List.of(productArticleDto1, productArticleDto2)).build();

        this.productDto2 = ProductDto.builder().name("Product 2").price(BigDecimal.ZERO)
                .productArticles(List.of(productArticleDto2)).build();

        this.article1 = Article.builder().id(1L).name("Art 1").stock(5L).build();
        this.article2 = Article.builder().id(2L).name("Art 2").stock(10L).build();

        this.product1 = Product.builder().id(1L).name("Product 1").price(BigDecimal.ZERO).build();
        this.product2 = Product.builder().id(2L).name("Product 2").price(BigDecimal.ZERO).build();

        this.productArticle1 = ProductArticle.builder().id(1L).product(product1).article(article1).quantity(2L).build();
        this.productArticle2 = ProductArticle.builder().id(2L).product(product1).article(article2).quantity(1L).build();
        this.productArticle3 = ProductArticle.builder().id(3L).product(product2).article(article1).quantity(15L).build();
    }


    @Test
    public void testProcessProducts_EmptyInput() throws Exception {
        List<ProductDto> input = List.of();

        Assertions.assertThat(productService.processProducts(input)).isEmpty();
    }

    @Test
    @DisplayName("processProducts :: Articles found, no exception should be thrown")
    public void testProcessProducts_ArticlesFound() {
        List<ProductDto> input = List.of(productDto1, productDto2);
        // Given
        given(articleRepository.findById(productArticleDto1.getArticleId())).willReturn(Optional.of(article1));
        given(articleRepository.findById(productArticleDto2.getArticleId())).willReturn(Optional.of(article2));

        assertDoesNotThrow(() -> productService.processProducts(input));
    }

    @Test
    @DisplayName("processProducts :: Articles not found, exception should be thrown")
    public void testProcessProducts_ArticlesNotFound() {
        List<ProductDto> input = List.of(productDto1, productDto2);
        // Given
        given(articleRepository.findById(productArticleDto1.getArticleId())).willReturn(Optional.of(article1));
        given(articleRepository.findById(productArticleDto2.getArticleId())).willReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> productService.processProducts(input));
    }

    @Test
    @DisplayName("processProducts :: Successfully save the products")
    public void testProcessProducts_Success() throws Exception {
        List<ProductDto> input = List.of(productDto1, productDto2);
        // Given
        given(articleRepository.findById(productArticleDto1.getArticleId())).willReturn(Optional.of(article1));
        given(articleRepository.findById(productArticleDto2.getArticleId())).willReturn(Optional.of(article2));

        assertDoesNotThrow(() -> productService.processProducts(input));
        Assertions.assertThat(productService.processProducts(input)).isNotEmpty().hasSize(2);
    }

    @Test
    @DisplayName("calculateStock :: Successfully calculate product stock")
    public void testCalculateStock_Success() {
        // Given
        given(productArticleRepository.findAll()).willReturn(List.of(productArticle1, productArticle2, productArticle3));

        // When
        List<Product> products = productService.calculateProductStock();

        // then
        assertThat(products)
                .isNotEmpty()
                .hasSize(2)
                .extracting(Product::getName, Product::getStock)
                .containsExactlyInAnyOrder(
                        tuple("Product 1", 2L),
                        tuple("Product 2", 0L)
                );
    }

    @Test
    @DisplayName("calculateStock :: No data in database")
    public void testCalculateStock_NoData() {
        // Given
        given(productArticleRepository.findAll()).willReturn(List.of());

        // When
        List<Product> products = productService.calculateProductStock();

        // then
        assertThat(products).isEmpty();
    }


    @Test
    @DisplayName("updateSoldProductInventory :: Successfully update the inventory")
    public void testUpdateSoldProductInventory_Success() throws ValidationException {
        // Given
        given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
        given(productArticleRepository.findAllByProduct(product1)).willReturn(List.of(productArticle1, productArticle2));

        // When
        Product products = productService.updateSoldProductInventory(1L);

        // then
        assertDoesNotThrow(() -> productService.updateSoldProductInventory(1L));
    }

    @Test
    @DisplayName("updateSoldProductInventory :: Product not found, should throw ValidationException")
    public void testUpdateSoldProductInventory_ProductNotFound() {
        // Given
        given(productRepository.findById(product1.getId())).willReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> productService.updateSoldProductInventory(1L));
    }

    @Test
    @DisplayName("updateSoldProductInventory :: Sufficient articles not found, should throw ValidationException")
    public void testUpdateSoldProductInventory_SufficientArticlesProductNotFound() {
        // Given
        given(productRepository.findById(product2.getId())).willReturn(Optional.of(product2));
        given(productArticleRepository.findAllByProduct(product2)).willReturn(List.of(productArticle3));

        assertThrows(ValidationException.class, () -> productService.updateSoldProductInventory(2L));
    }

}
