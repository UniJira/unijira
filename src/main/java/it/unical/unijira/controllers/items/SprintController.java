package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.user.SprintDTO;
import it.unical.unijira.services.common.SprintInsertionService;
import it.unical.unijira.services.common.SprintService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sprint")
public class SprintController implements CrudController<SprintDTO, Long> {

    private final SprintService sprintService;
    private final SprintInsertionService insertionService;

    @Autowired
    public SprintController(SprintService sprintService,
                             SprintInsertionService insertionService) {
        this.sprintService = sprintService;
        this.insertionService = insertionService;
    }

    @Override
    public ResponseEntity<List<SprintDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<SprintDTO> read(ModelMapper modelMapper, Long id) {
        return null;
    }

    @Override
    public ResponseEntity<SprintDTO> create(ModelMapper modelMapper, SprintDTO dto) {
        return null;
    }

    @Override
    public ResponseEntity<SprintDTO> update(ModelMapper modelMapper, Long id, SprintDTO dto) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> delete(Long id) {
        return null;
    }
}
