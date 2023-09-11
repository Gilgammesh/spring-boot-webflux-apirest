package com.santander.springbootwebfluxapirest.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.santander.springbootwebfluxapirest.entity.Producto;

import reactor.core.publisher.Mono;

public interface ProductoRepository extends ReactiveMongoRepository<Producto, String> {

    public Mono<Producto> findByNombre(String nombre);

    @Query("{ 'nombre': ?0 }")
    public Mono<Producto> obtenerPorNombre(String nombre);

}
