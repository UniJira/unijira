package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ItemRepository;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.Item;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.common.ItemService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public record ItemServiceImpl(ItemRepository pbiRepository, UserRepository userRepository)
        implements ItemService {

     public Optional<Item> save (Item pbi){
        return Optional.of(pbiRepository.saveAndFlush(pbi));
    }

    @Override
    public Optional<Item> update(Long id, Item pbi) {
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
    public void delete(Item pbi) {
         pbiRepository.delete(pbi);

    }

    @Override
    public Optional<Item> findById(Long id) {
        return pbiRepository.findById(id);
    }

    @Override
    public List<Item> findAll() {
        return StreamSupport.stream(pbiRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByFather(Long fatherId, int page, int size) {
        Optional<Item> father = pbiRepository.findById(fatherId);
        if (father.get() != null )
            return pbiRepository.findAllByFather(father.get(), PageRequest.of(page, size));
        return Collections.emptyList();
     }

    @Override
    public List<Item> findAllByUser(Long userId, int page, int size) {
        Optional<User> assignee = userRepository.findById(userId);
        if (assignee.get() != null)
            return pbiRepository.findAllByAssignee(assignee.get());
        return Collections.emptyList();
    }


}




