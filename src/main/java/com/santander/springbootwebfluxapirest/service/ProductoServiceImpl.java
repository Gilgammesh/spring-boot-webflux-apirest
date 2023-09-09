package com.santander.springbootwebfluxapirest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.santander.springbootwebfluxapirest.entity.Categoria;
import com.santander.springbootwebfluxapirest.entity.Producto;
import com.santander.springbootwebfluxapirest.repository.CategoriaRepository;
import com.santander.springbootwebfluxapirest.repository.ProductoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Override
	public Flux<Producto> findAll() {
		return productoRepository.findAll();
	}

	@Override
	public Mono<Producto> findById(String id) {
		return productoRepository.findById(id);
	}

	@Override
	public Mono<Producto> save(Producto producto) {
		return productoRepository.save(producto);
	}

	@Override
	public Mono<Void> delete(Producto producto) {
		return productoRepository.delete(producto);
	}

	@Override
	public Flux<Producto> findAllConNombreUpperCase() {
		return productoRepository.findAll().map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
	}

	@Override
	public Flux<Producto> findAllConNombreUpperCaseRepeat() {
		return findAllConNombreUpperCase().repeat(5000);
	}

	@Override
	public Flux<Categoria> findAllCategoria() {
		return categoriaRepository.findAll();
	}

	@Override
	public Mono<Categoria> findCategoriaById(String id) {
		return categoriaRepository.findById(id);
	}

	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

}
