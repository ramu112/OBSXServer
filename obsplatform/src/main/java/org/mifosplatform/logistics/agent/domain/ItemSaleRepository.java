package org.mifosplatform.logistics.agent.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemSaleRepository extends JpaRepository<ItemSale, Long>,
JpaSpecificationExecutor<ItemSale>{

}
