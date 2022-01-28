package it.unical.unijira.data.dao.items;

import it.unical.unijira.data.models.items.ItemStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemStatusHistoryRepository extends JpaRepository<ItemStatusHistory, Long> {
    @Query("from ItemStatusHistory history, ProductBacklogInsertion pbi where pbi.item.id = history.item.id and pbi.backlog.project.id = :id")
    List<ItemStatusHistory> findAllByItemProjectIdAndDateRange(Long id);
}
