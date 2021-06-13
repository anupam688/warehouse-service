package com.demo.warehouseservice.service;

import com.demo.warehouseservice.exception.ValidationException;
import com.demo.warehouseservice.repository.ArticleRepository;
import com.demo.warehouseservice.repository.ProductArticleRepository;
import com.demo.warehouseservice.repository.ProductRepository;
import com.demo.warehouseservice.dto.ProductArticleDto;
import com.demo.warehouseservice.dto.ProductDto;
import com.demo.warehouseservice.model.Article;
import com.demo.warehouseservice.model.Product;
import com.demo.warehouseservice.model.ProductArticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ProductService {

    private final ArticleRepository articleDao;
    private final ProductRepository productDao;
    private final ProductArticleRepository productArticleDao;

    @Autowired
    public ProductService(ArticleRepository articleDao,
                          ProductRepository productDao,
                          ProductArticleRepository productArticleDao) {
        this.articleDao = articleDao;
        this.productDao = productDao;
        this.productArticleDao = productArticleDao;
    }

    /**
     * Gets product.
     *
     * @param id the id
     * @return the product
     */
    public Optional<Product> getProduct(final Long id) {
        return productDao.findById(id);
    }

    /**
     * Gets all products.
     *
     * @return the list of all products
     */
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    /**
     * Process product
     *
     * @param productDto the product dto
     * @return the product
     * @throws ValidationException the validation exception
     */
    @Transactional
    public Product processProduct(final ProductDto productDto) throws ValidationException {
        List<ProductArticle> productArticles = new ArrayList<>();
        Product product = Product.builder().name(productDto.getName()).price(productDto.getPrice()).build();
        for (ProductArticleDto productArticleDto : productDto.getProductArticles()) {
            Article article = articleDao.findById(productArticleDto.getArticleId())
                    .orElseThrow(() -> new ValidationException("Article not found :: " + productArticleDto.getArticleId()));
            productArticles.add(ProductArticle.builder()
                    .product(product).article(article).quantity(productArticleDto.getQuantity()).build());
        }
        productArticleDao.saveAll(productArticles);
        return product;
    }

    /**
     * Process products list.
     *
     * @param productDtos the product dtos
     * @return the list
     * @throws ValidationException the validation exception
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Product> processProducts(List<ProductDto> productDtos) throws ValidationException {
        List<Product> result = new ArrayList<>();
        for (ProductDto productDto : productDtos) {
            Product product = this.processProduct(productDto);
            result.add(product);
        }
        return result;
    }

    private final BiFunction<Long, ProductArticle, Long> calculateStock = (intermediateResult, productArticle) -> {
        Article article = productArticle.getArticle();
        Long quantity = productArticle.getQuantity();

        return Math.min(intermediateResult, article.getStock() / quantity);
    };

    /**
     * Calculate product stock list.
     *
     * @return the list
     */
    public List<Product> calculateProductStock() {
        return productArticleDao.findAll().stream()
                .collect(Collectors.groupingBy(ProductArticle::getProduct))
                .entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    Long stock = entry.getValue().stream()
                            .reduce(Long.MAX_VALUE, calculateStock, Math::min);
                    product.setStock(stock);
                    return product;
                }).collect(Collectors.toList());
    }

    /**
     * Update sold product inventory.
     *
     * @param id the product id
     * @return the product
     * @throws ValidationException if conditions are not met.
     */
    @Transactional(rollbackFor = Exception.class)
    public Product updateSoldProductInventory(final Long id) throws ValidationException {
        Product product = productDao.findById(id)
                .orElseThrow(() -> new ValidationException("Product not found!! :: " + id));

        List<ProductArticle> productArticles = productArticleDao.findAllByProduct(product);

        for (ProductArticle productArticle : productArticles) {
            Article article = productArticle.getArticle();
                if (article.getStock() - productArticle.getQuantity() < 0) {
                    throw new ValidationException("Product out of stock!!");
                }
                article.setStock(article.getStock() - productArticle.getQuantity());
                articleDao.save(article);
        }

        return product;
    }
}
