package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.user.ProductBacklogDTO;
import it.unical.unijira.services.common.ProductBacklogInsertionService;
import it.unical.unijira.services.common.ProductBacklogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backlog")
public class ProductBacklogController implements CrudController<ProductBacklogDTO, Long> {


    private final ProductBacklogService backlogService;
    private final ProductBacklogInsertionService insertionService;

    @Autowired
    public ProductBacklogController(ProductBacklogService backlogService,
                                    ProductBacklogInsertionService insertionService) {
        this.backlogService = backlogService;
        this.insertionService = insertionService;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {
        return null;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> read(ModelMapper modelMapper, Long id) {
        return null;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> create(ModelMapper modelMapper, ProductBacklogDTO dto) {
        return null;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductBacklogDTO> update(ModelMapper modelMapper, Long id, ProductBacklogDTO dto) {
        return null;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> delete(Long id) {
        return null;
    }
}
