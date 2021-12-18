package it.unical.unijira.services.common;

import it.unical.unijira.data.models.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<Item> save (Item pbi);
    Optional<Item> update (Long id, Item pbi);
    void delete(Item pbi);
    Optional<Item> findById(Long id);
    List<Item> findAll();
    List<Item> findAllByFather(Long fatherId, int page, int size);
    List<Item> findAllByUser(Long userId, int page, int size);

}
