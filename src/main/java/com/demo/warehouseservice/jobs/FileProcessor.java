package com.demo.warehouseservice.jobs;


import com.demo.warehouseservice.exception.ValidationException;
import com.demo.warehouseservice.service.ArticleService;
import com.demo.warehouseservice.service.ProductService;
import com.demo.warehouseservice.dto.ArticleDto;
import com.demo.warehouseservice.dto.ProductDto;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class FileProcessor {

    private final ArticleService articleService;
    private final ProductService productService;

    @Autowired
    public FileProcessor(ArticleService articleService,
                         ProductService productService) {
        this.articleService = articleService;
        this.productService = productService;
    }


    public void processFile(InputStream stream) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            // create `JsonReader` instance
            JsonReader reader = new JsonReader(inputStreamReader);
            Gson gson = new Gson();

            // read data
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "inventory" -> {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            ArticleDto articleDto = gson.fromJson(reader, ArticleDto.class);
                            log.debug(articleDto.toString());
                            articleService.processArticle(articleDto);
                        }
                        reader.endArray();
                    }
                    case "products" -> {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            ProductDto productDto = gson.fromJson(reader, ProductDto.class);
                            try {
                                log.debug(productDto.toString());
                                productService.processProduct(productDto);
                            } catch (ValidationException exception) {
                                log.error("Exception while processing product :: ", exception);
                            }
                        }
                        reader.endArray();
                    }
                    default -> reader.skipValue();
                }
            }
            reader.endObject();
            reader.close();
        } catch (IOException e) {
            log.error("Error encountered while processing file!!", e);
        }
    }



}
