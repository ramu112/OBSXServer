package org.mifosplatform.organisation.mapping.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChannelMappingRepository  extends JpaRepository<ChannelMapping, Long>,
JpaSpecificationExecutor<ChannelMapping>{

}
