package org.mifosplatform.portfolio.service.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetail, Long>,
JpaSpecificationExecutor<ServiceDetail>{

}
