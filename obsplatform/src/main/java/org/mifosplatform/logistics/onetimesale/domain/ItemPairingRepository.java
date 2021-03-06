package org.mifosplatform.logistics.onetimesale.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemPairingRepository extends JpaRepository<ItemPairing, Long>, JpaSpecificationExecutor<ItemPairing> {

}
