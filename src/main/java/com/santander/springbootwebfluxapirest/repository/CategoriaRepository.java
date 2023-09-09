package com.santander.springbootwebfluxapirest.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.santander.springbootwebfluxapirest.entity.Categoria;

public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {

}
