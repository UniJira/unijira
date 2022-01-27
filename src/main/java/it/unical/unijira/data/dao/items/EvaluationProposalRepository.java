package it.unical.unijira.data.dao.items;

import it.unical.unijira.data.models.items.EvaluationProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationProposalRepository extends JpaRepository<EvaluationProposal, Long> {

}
