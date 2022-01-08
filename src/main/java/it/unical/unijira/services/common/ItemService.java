package it.unical.unijira.services.common;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.projects.Project;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<Item> save (Item pbi);
    Optional<Item> update (Long id, Item pbi);
    void delete(Item pbi);
    Optional<Item> findById(Long id);
    List<Item> findAll();
    List<Item> findAllByType(String type);
    List<Item> findAllByFather(Long fatherId, int page, int size);
    List<Item> findAllByUser(Long userId, int page, int size);
    List<Item> findAllByProjectNoFather(Project project, int page, int size);
    List<Item> findAllByBacklogNoFather(ProductBacklog backlog, int page, int size);
    List<Item> findAllBySprintNoFather(Sprint sprint, int page, int size);
    List<Item> finAllByRoadmapNoFather(Roadmap roadmap, int page, int size);
}
