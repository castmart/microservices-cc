package com.castmart.AggregatorMicroservice.controller;

import com.castmart.AggregatorMicroservice.model.DayStatistic;
import com.castmart.AggregatorMicroservice.model.Product;
import com.castmart.AggregatorMicroservice.persistence.ProductJPARepository;
import com.castmart.AggregatorMicroservice.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping( path = "products")
public class ProductsController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductsController.class);

    ProductJPARepository jpaRepo;

    StatisticsService statsService;

    @Autowired
    public ProductsController(StatisticsService service, ProductJPARepository repository) {
        this.jpaRepo = repository;
        this.statsService = service;
        LOG.info("ProductsController created...");
    }

    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Product>> productsFromDb() {
        return ResponseEntity.ok(jpaRepo.findAll());
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<Product> createProduct(@RequestBody Product p) {
        if (p.getId() == null)
            p.setId(UUID.randomUUID().toString());
        p.setCreationTimestamp(null);
        p.setEditionTimestamp(null);
        return ResponseEntity.ok(jpaRepo.save(p));
    }

    @GetMapping(path = "{productId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Product> productById(@PathVariable("productId") String productId) {
            UUID id = UUID.fromString(productId);
            Optional<Product> product = jpaRepo.findById(productId);
            if (product.isPresent())
                return ResponseEntity.ok(product.get());
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(path="statistic/{day}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<DayStatistic> getStatistics(@PathVariable("day") String day) throws ParseException {
        return ResponseEntity.ok(statsService.getStatisticOfDay(day));

    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> badDate(ParseException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("Invalid Date format", "The correct format is yyyy-MM-dd."));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> badUUID(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("Invalid uuid", "The provided id is not a valid UUID."));
    }

    static class ErrorDTO implements Serializable {
        private String error;
        private String description;

        public ErrorDTO() {}

        public ErrorDTO(String error, String description) {
            this.setError(error);
            this.setDescription(description);
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}