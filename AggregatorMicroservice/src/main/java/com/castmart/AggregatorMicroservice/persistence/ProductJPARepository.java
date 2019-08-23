package com.castmart.AggregatorMicroservice.persistence;

import com.castmart.AggregatorMicroservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJPARepository extends JpaRepository<Product, String> {

}
