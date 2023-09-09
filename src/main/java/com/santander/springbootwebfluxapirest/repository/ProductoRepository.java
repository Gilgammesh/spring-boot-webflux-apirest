package com.santander.springbootwebfluxapirest.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.santander.springbootwebfluxapirest.entity.Producto;

public interface ProductoRepository extends ReactiveMongoRepository<Producto, String> {

}
