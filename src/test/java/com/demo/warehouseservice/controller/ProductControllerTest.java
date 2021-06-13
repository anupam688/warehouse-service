package com.demo.warehouseservice.controller;

import com.demo.warehouseservice.jobs.FileProcessor;
import com.demo.warehouseservice.model.Product;
import com.demo.warehouseservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;


import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    public static final String PRODUCTS_URL = "/products";
    public static final String TEST_RESOURCE_PATH = "classpath:test_resource/products/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private FileProcessor fileProcessor;

    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {
        this.product1 = Product.builder().id(1L).name("Table").price(BigDecimal.TEN).stock(2L).build();
        this.product2 = Product.builder().id(2L).name("Chair").price(BigDecimal.ONE).stock(0L).build();
    }

    @Test
    @DisplayName("POST : /products : Success")
    public void test_POST_importProducts_Success() throws Exception {
        // Given
        given(productService.processProducts(anyList())).willReturn(List.of(product1, product2));

        File file = ResourceUtils.getFile(TEST_RESOURCE_PATH + "TEST_product_inventory_success.json");
        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart(PRODUCTS_URL)
                        .file("file",Files.readAllBytes(file.toPath()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_POST_importProducts_json_BadRequest() throws Exception {
        File file = ResourceUtils.getFile(TEST_RESOURCE_PATH + "TEST_product_inventory_json_bad_request.json");

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart(PRODUCTS_URL)
                        .file("file",Files.readAllBytes(file.toPath()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("GET : /products/{id} : Success")
    public void test_GET_getProduct_Success() throws Exception {
        // Given
        given(productService.getProduct(1L)).willReturn(Optional.of(product1));

        this.mockMvc
                .perform(get("/products/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Table")));
    }

    @Test
    @DisplayName("GET : /products/{id} : NotFound")
    public void test_GET_getProduct_NotFound() throws Exception {
        // Given
        given(productService.getProduct(1L)).willReturn(Optional.empty());

        this.mockMvc
                .perform(get("/products/{id}", 1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET : /products/stock : Success")
    public void test_GET_getProductStock() throws Exception {
        // Given
        given(productService.calculateProductStock()).willReturn(List.of(product1, product2));

        this.mockMvc
                .perform(get("/products/stock"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Table", "Chair")))
                .andExpect(jsonPath("$[*].stock", containsInAnyOrder(2, 0)));
    }

    @Test
    @DisplayName("PUT : /products/{id}/sell : Success")
    public void test_PUT_sellProduct() throws Exception {
        // Given
        given(productService.updateSoldProductInventory(1L)).willReturn(product1);

        this.mockMvc
                .perform(put("/products/{id}/sell", 1).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Table")));
    }
}
