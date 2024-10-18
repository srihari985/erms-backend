package com.erms.ERMS_Application.Quotation.AddParty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddPartyRepo extends JpaRepository<AddPartyEntity, Long>{

}
