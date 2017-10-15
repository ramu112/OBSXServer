package org.mifosplatform.organisation.channel.service;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.channel.data.ChannelData;


public interface ChannelReadPlatformService {

	Page<ChannelData> retrieveChannel(SearchSqlQuery searchChannel);
	
	ChannelData retrieveChannel(Long channelId);

}
