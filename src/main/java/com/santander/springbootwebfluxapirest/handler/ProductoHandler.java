package com.santander.springbootwebfluxapirest.handler;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.santander.springbootwebfluxapirest.entity.Producto;
import com.santander.springbootwebfluxapirest.service.ProductoService;

@RequiredArgsConstructor
@Component
public class ProductoHandler {

    private final ProductoService productoService;

    private final Validator validator;

    @Value("${config.uploads.path}")
    private String path;

    public Mono<ServerResponse> listar(ServerRequest request) {
        Flux<Producto> productos = productoService.findAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(productos, Producto.class);
    }

    public Mono<ServerResponse> detail(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Producto> productoDb = productoService.findById(id);
        return productoDb.flatMap(p -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> crear(ServerRequest request) {
        Mono<Producto> producto = request.bodyToMono(Producto.class);
        return producto.flatMap(p -> {
            Errors errors = new BeanPropertyBindingResult(p, Producto.class.getName());
            validator.validate(p, errors);

            if (errors.hasErrors()) {
                return Flux.fromIterable(errors.getFieldErrors())
                        .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .collectList()
                        .flatMap(list -> ServerResponse.badRequest().bodyValue(list));
            } else {
                if (p.getCreateAt() == null) {
                    p.setCreateAt(new Date());
                }
                return productoService.save(p)
                        .flatMap(pdb -> ServerResponse.created(
                                request.uri().resolve(pdb.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(pdb));
            }
        });
    }

    public Mono<ServerResponse> editar(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Producto> producto = request.bodyToMono(Producto.class);
        Mono<Producto> productoDb = productoService.findById(id);

        return productoDb.zipWith(producto, (db, req) -> {
            if (req.getNombre() != null) {
                db.setNombre(req.getNombre());
            }
            if (req.getPrecio() != null) {
                db.setPrecio(req.getPrecio());
            }
            if (req.getCategoria() != null) {
                db.setCategoria(req.getCategoria());
            }
            return db;
        }).flatMap(p -> ServerResponse.created(
                request.uri().resolve(p.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.save(p), Producto.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Producto> productoDb = productoService.findById(id);

        return productoDb.flatMap(p -> productoService.delete(p)
                .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> upload(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class).flatMap(file -> productoService.findById(id)
                        .flatMap(p -> {
                            p.setFoto(UUID.randomUUID().toString() + "-"
                                    + file.filename()
                                            .replace(" ", "")
                                            .replace(":", "")
                                            .replace("\\", ""));
                            return file.transferTo(new File(path + p.getFoto()))
                                    .then(productoService.save(p));
                        }))
                .flatMap(p -> ServerResponse.created(
                        request.uri().resolve(p.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
