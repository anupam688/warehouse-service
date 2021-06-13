package com.demo.warehouseservice.controller;

import com.demo.warehouseservice.exception.ValidationException;
import com.demo.warehouseservice.jobs.FileProcessor;
import com.demo.warehouseservice.model.Article;
import com.demo.warehouseservice.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final FileProcessor fileProcessor;

    @Autowired
    public ArticleController(ArticleService articleService,FileProcessor fileProcessor) {

        this.articleService = articleService;
        this.fileProcessor = fileProcessor;
    }


    /**
     * Import articles file response entity.
     *
     * @param file the file
     * @throws ValidationException the validation exception
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void importArticlesFile(@RequestParam MultipartFile file)
            throws ValidationException, IOException {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File not found or File empty!!");
        }
        fileProcessor.processFile(file.getInputStream());
    }

    /**
     * Gets all articles.
     *
     * @return list of articles.
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }
}
