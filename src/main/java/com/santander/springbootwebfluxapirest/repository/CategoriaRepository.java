package com.santander.springbootwebfluxapirest.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.santander.springbootwebfluxapirest.entity.Categoria;

import reactor.core.publisher.Mono;

public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {

    public Mono<Categoria> findByNombre(String nombre);

}
