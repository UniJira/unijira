package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogItemRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.common.ProductBacklogItemService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public record ProductBacklogItemServiceImpl(ProductBacklogItemRepository pbiRepository)
        implements ProductBacklogItemService {

     public Optional<ProductBacklogItem> save (ProductBacklogItem pbi){
        return Optional.of(pbiRepository.saveAndFlush(pbi));
    }

    @Override
    public Optional<ProductBacklogItem> update(Long id, ProductBacklogItem pbi) {
        return pbiRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                    updatedItem.setTags(pbi.getTags());
                    updatedItem.setDescription(pbi.getDescription());
                    updatedItem.setUpdatedAt(LocalDateTime.now());
                    updatedItem.setEvaluation(pbi.getEvaluation());
                    updatedItem.setNotes(pbi.getNotes());
                    updatedItem.setAssignees(pbi.getAssignees());
                    try {
                        updatedItem.setFather(pbi.getFather());
                    } catch (NonValidItemTypeException e) {
                        throw new RuntimeException(e.getErrorMessage());
                    }
                    updatedItem.setOwner(pbi.getOwner());
                    updatedItem.setSummary(pbi.getSummary());
                    try {
                        updatedItem.setType(pbi.getType());
                    } catch (NonValidItemTypeException e) {
                        throw new RuntimeException(e.getErrorMessage());
                    }
                    pbi.setMeasureUnit(pbi.getMeasureUnit());
                })
                .findFirst()
                .map(pbiRepository::saveAndFlush);
    }

    @Override
    public void delete(ProductBacklogItem pbi) {
         pbiRepository.delete(pbi);

    }

    @Override
    public Optional<ProductBacklogItem> findById(Long id) {
        return pbiRepository.findById(id);
    }

    @Override
    public List<ProductBacklogItem> findAll() {
        return StreamSupport.stream(pbiRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductBacklogItem> findAllByFather(ProductBacklogItem father) {
        return pbiRepository.findAllByFather(father);
    }


}




