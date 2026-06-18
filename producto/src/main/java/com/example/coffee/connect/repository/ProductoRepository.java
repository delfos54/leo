package com.example.coffee.connect.repository;

import com.example.coffee.connect.model.Productos; //
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Productos, Long> {
   
}



