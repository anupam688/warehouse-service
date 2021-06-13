package com.demo.warehouseservice.repository;

import com.demo.warehouseservice.model.Product;
import com.demo.warehouseservice.model.ProductArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductArticleRepository extends JpaRepository<ProductArticle, Long> {

    /**
     * Find all ProductArticles by product.
     *
     * @param product the product
     * @return the list
     */
    List<ProductArticle> findAllByProduct(Product product);

}
