package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.SprintRepository;
import it.unical.unijira.services.common.SprintService;
import org.springframework.stereotype.Service;

@Service
public record SprintServiceImpl(SprintRepository sprintRepository)
        implements SprintService {
}
