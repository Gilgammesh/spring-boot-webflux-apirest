package com.santander.springbootwebfluxapirest;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.santander.springbootwebfluxapirest.entity.Categoria;
import com.santander.springbootwebfluxapirest.entity.Producto;
import com.santander.springbootwebfluxapirest.service.ProductoService;

import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SpringBootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductoService service;

	@Test
	void listarTest() {
		client.get().uri("/api/v2/productos").accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Producto.class)
				// .hasSize(9)
				.consumeWith(response -> {
					List<Producto> productos = response.getResponseBody();
					Assertions.assertThat(productos.size()).isSameAs(10);
				});
	}

	@Test
	void detailTest() {
		Producto producto = service.findByNombre("Apple iPod").block();

		client.get().uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
				.accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON)
				.expectBody(Producto.class)
				// .jsonPath("$.id").isNotEmpty()
				// .jsonPath("$.nombre").isEqualTo("Apple iPod")
				.consumeWith(response -> {
					Producto p = response.getResponseBody();
					Assertions.assertThat(p.getId()).isNotEmpty();
					Assertions.assertThat(p.getNombre()).isEqualTo("Apple iPod");
				});
	}

	@Test
	void crearTest() {
		Categoria categoria = service.findCategoriaByNombre("Muebles").block();

		Producto producto = new Producto("Google Pixel", 999.99, categoria);

		client.post().uri("/api/v2/productos")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(producto), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Producto.class)
				// .jsonPath("$.id").isNotEmpty()
				// .jsonPath("$.nombre").isEqualTo("Google Pixel")
				// .jsonPath("$.precio").isEqualTo(999.99)
				.consumeWith(response -> {
					Producto p = response.getResponseBody();
					Assertions.assertThat(p.getId()).isNotEmpty();
					Assertions.assertThat(p.getNombre()).isEqualTo("Google Pixel");
					Assertions.assertThat(p.getPrecio()).isEqualTo(999.99);
					Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("Muebles");
				});
		;
	}

	@Test
	void editarTest() {
		Producto producto = service.findByNombre("Sony Camara HD Digital").block();

		Categoria categoria = service.findCategoriaByNombre("Computaci\u00F3n").block();

		Producto newProducto = new Producto("Samsung Galaxy Tab", 1800.00, categoria);

		client.put().uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(newProducto), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Producto.class)
				// .jsonPath("$.id").isNotEmpty()
				// .jsonPath("$.nombre").isEqualTo("Samsung Galaxy Tab")
				// .jsonPath("$.precio").isEqualTo(1800.00)
				.consumeWith(response -> {
					Producto p = response.getResponseBody();
					Assertions.assertThat(p.getId()).isNotEmpty();
					Assertions.assertThat(p.getNombre()).isEqualTo("Samsung Galaxy Tab");
					Assertions.assertThat(p.getPrecio()).isEqualTo(1800.00);
					Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("Computaci\u00F3n");
				});

	}

	@Test
	void eliminarTest() {
		Producto producto = service.findByNombre("Sony Notebook").block();

		client.delete().uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus().isNoContent()
				.expectBody()
				.isEmpty();

		client.get().uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.isEmpty();
	}

	@Test
	void uploadTest() {
		Producto producto = service.findByNombre("TV Sony Bravia OLED 4K Ultra HD").block();

		client.post().uri("/api/v2/productos/upload/{id}", Collections.singletonMap("id", producto.getId()))
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(new ClassPathResource("apple-ipod-nano.jpg"))
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.foto").isNotEmpty()
				.consumeWith(response -> {
					Producto p = service.findById(producto.getId()).block();
					Assertions.assertThat(p.getFoto()).isNotEmpty();
					Assertions.assertThat(p.getFoto()).isEqualTo("apple-ipod-nano.jpg");
				});
	}

}
