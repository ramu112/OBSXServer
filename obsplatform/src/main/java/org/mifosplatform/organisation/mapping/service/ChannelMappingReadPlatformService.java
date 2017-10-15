package org.mifosplatform.organisation.mapping.service;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.mapping.data.ChannelMappingData;

public interface ChannelMappingReadPlatformService {

	Page<ChannelMappingData> retrieveChannelMapping(SearchSqlQuery searchChannelMapping);
	ChannelMappingData retrieveChannelMapping(Long channelmappingId);

}
