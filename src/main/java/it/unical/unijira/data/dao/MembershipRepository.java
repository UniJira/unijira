package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Membership;
import it.unical.unijira.data.models.MembershipKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipKey>, JpaSpecificationExecutor<Membership> {

}
