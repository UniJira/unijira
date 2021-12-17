package it.unical.unijira.controllers.items;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.user.RoadmapDTO;
import it.unical.unijira.services.common.RoadmapInsertionService;
import it.unical.unijira.services.common.RoadmapService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roadmap")
public class RoadmapController implements CrudController<RoadmapDTO, Long> {

    private final RoadmapService roadmapService;
    private final RoadmapInsertionService insertionService;

    @Autowired
    public RoadmapController(RoadmapService roadmapService,
                             RoadmapInsertionService insertionService) {
        this.roadmapService = roadmapService;
        this.insertionService = insertionService;
    }

    @Override
    public ResponseEntity<List<RoadmapDTO>> readAll(ModelMapper modelMapper, Integer page, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<RoadmapDTO> read(ModelMapper modelMapper, Long id) {
        return null;
    }

    @Override
    public ResponseEntity<RoadmapDTO> create(ModelMapper modelMapper, RoadmapDTO dto) {
        return null;
    }

    @Override
    public ResponseEntity<RoadmapDTO> update(ModelMapper modelMapper, Long id, RoadmapDTO dto) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> delete(Long id) {
        return null;
    }
}
