package it.unical.unijira.data.dao.projects;

import it.unical.unijira.data.models.projects.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByProjectId(Long id);

}
