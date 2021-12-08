package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.user.ProductBacklogItemDTO;
import it.unical.unijira.services.common.ProductBacklogItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backlog/item")
public class ProductBacklogItemController implements CrudController<ProductBacklogItemDTO, Long> {

    private final ProductBacklogItemService pbiService;

    @Autowired
    public ProductBacklogItemController(ProductBacklogItemService pbiService) {
        this.pbiService = pbiService;
    }


    @Override
    public ResponseEntity<List<ProductBacklogItemDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<ProductBacklogItemDTO> read(ModelMapper modelMapper, Long id) {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<ProductBacklogItemDTO> create(ModelMapper modelMapper, ProductBacklogItemDTO dto) {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<ProductBacklogItemDTO> update(ModelMapper modelMapper, Long id, ProductBacklogItemDTO dto) {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<Boolean> delete(Long id) {
        //TODO
        return null;
    }


    @GetMapping("by_user/{user}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogItemDTO>> itemsByUser(ModelMapper modelMapper, @PathVariable Long user, Integer page, Integer size) {

       //TODO
        return null;

    }

    @GetMapping("{father}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductBacklogItemDTO>> itemsByFather(ModelMapper modelMapper, @PathVariable Long father, Integer page, Integer size){
        // TODO
        return null;
    }



}
