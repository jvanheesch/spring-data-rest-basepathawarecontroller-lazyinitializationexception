package com.github.jvanheesch;

import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@BasePathAwareController
public class MyBasePathAwareController {
    private final BookRepository bookRepository;

    public MyBasePathAwareController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @RequestMapping(value = "/books/{id}/test", method = GET, produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PersistentEntityResource> test(@PathVariable("id") Long id, PersistentEntityResourceAssembler assembler) {
        return bookRepository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
