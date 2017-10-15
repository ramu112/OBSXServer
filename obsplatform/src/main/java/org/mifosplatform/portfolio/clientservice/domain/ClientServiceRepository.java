package org.mifosplatform.portfolio.clientservice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author trigital
 *
 */
public interface ClientServiceRepository extends JpaRepository<ClientService,Long >,JpaSpecificationExecutor<ClientService>{

}

