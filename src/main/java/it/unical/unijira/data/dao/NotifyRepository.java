package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Notify;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, Long>, JpaSpecificationExecutor<Notify> {
    List<Notify> findByUserIdOrderByReadAscPriorityAscDateDesc(Long id, Pageable pageable);
}
