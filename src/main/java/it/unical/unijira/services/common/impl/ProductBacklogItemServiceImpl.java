package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogItemRepository;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.common.ProductBacklogItemService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public record ProductBacklogItemServiceImpl(ProductBacklogItemRepository pbiRepository, UserRepository userRepository)
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
    public List<ProductBacklogItem> findAllByFather(Long fatherId, int page, int size) {
        Optional<ProductBacklogItem> father = pbiRepository.findById(fatherId);
        if (father.get() != null )
            return pbiRepository.findAllByFather(father.get(), PageRequest.of(page, size));
        return Collections.emptyList();
     }

    @Override
    public List<ProductBacklogItem> findAllByUser(Long userId, int page, int size) {
        Optional<User> assignee = userRepository.findById(userId);
        if (assignee.get() != null)
            return pbiRepository.findAllByAssignee(assignee.get());
        return Collections.emptyList();
    }


}




