package com.castmart.importer.controller;

import com.castmart.importer.model.Product;
import com.castmart.importer.service.ProductSender;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;

@RestController
public class ImporterController {

    private static final Logger logger = LoggerFactory.getLogger(ImporterController.class);

    @Autowired
    ProductSender productSender;

    private int counter = 0;

    @GetMapping(path = "product/create", produces = "application/json")
    @ResponseBody
    public String dummy() {
        productSender.sendMessage(new Product(UUID.randomUUID(), "Chips", "Chips", "xxx", true, "grams"));
        return "{\"key\":"+(++counter)+"}";
    }

    @ApiOperation(value = "CSV File of products to upload endpoint")
    @PostMapping(path = "upload/product", consumes = "multipart/form-data", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> productsFile(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                List<CompletableFuture<Boolean>> futures =  new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                logger.info("Reading file: ");
                String line = null;
                int limitCount = -1;

                while ( (line = reader.readLine()) != null) {
                    logger.info(line);
                    if (limitCount >= 0 && limitCount < 1000) {
                        futures.add(productSender.constructAndSendMessage(line));
                    }
                    limitCount++;
                }
                // JOIN
                // CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
                /*
                long processed = futures.stream().filter( e ->  {
                    try {
                        return e.get();
                    } catch (Exception ex) {
                        return false;
                    }
                }).count();
                */
                return ResponseEntity.ok("{\"lines\":"+limitCount+", \"processed\":"+/*processed*/limitCount+"}");
            } catch(IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> serviceUnavailableByRejectedExecution(RejectedExecutionException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorDTO("The server is busy, try latter", "The server is under high demand, please try latter"));
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
