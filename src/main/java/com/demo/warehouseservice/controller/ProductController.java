package com.demo.warehouseservice.controller;

import com.demo.warehouseservice.exception.ValidationException;
import com.demo.warehouseservice.jobs.FileProcessor;
import com.demo.warehouseservice.model.Product;
import com.demo.warehouseservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final FileProcessor fileProcessor;

    @Autowired
    public ProductController(ProductService productService,
                             FileProcessor fileProcessor) {
        this.productService = productService;
        this.fileProcessor = fileProcessor;
    }


    /**
     * Gets all products.
     *
     * @return all products
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }


    /**
     * Gets product.
     *
     * @param id the id
     * @return the product
     */
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProduct(id)
                .map(product -> ResponseEntity.ok().body(product))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Import products file response entity.
     *
     * @param file the file
     * @throws ValidationException the validation exception
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void importProductsFile(@RequestParam MultipartFile file)
            throws ValidationException, IOException {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File not found or File empty!!");
        }
        fileProcessor.processFile(file.getInputStream());
    }

    /**
     * Gets product stock.
     *
     * @return the product stock
     */
    @GetMapping(value = "/stock", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Product> getProductStock() {
        return productService.calculateProductStock();
    }


    /**
     * Sell product.
     *
     * @param id the product id
     * @return the product
     * @throws ValidationException the validation exception
     */
    @PutMapping(value = "/{id}/sell", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Product sellProduct(@PathVariable Long id) throws ValidationException {
        return productService.updateSoldProductInventory(id);
    }
}
