package org.mifosplatform.organisation.mapping.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mapping.data.ChannelMappingData;
import org.mifosplatform.organisation.mapping.service.ChannelMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/channelmapping")
@Component
@Scope("singleton")
public class ChannelMappingApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	
	private final static  String RESOURCE_TYPE = "CHANNELMAPPING";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<ChannelMappingData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private ChannelMappingReadPlatformService channelMappingReadPlatformService;
	

	@Autowired
	public ChannelMappingApiResource(PlatformSecurityContext context,
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer<ChannelMappingData> apiJsonSerializer,
			ApiRequestParameterHelper apiRequestParameterHelper,
			ChannelMappingReadPlatformService channelMappingReadPlatformService) {
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.channelMappingReadPlatformService = channelMappingReadPlatformService;
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String ChannelMappingDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchChannelMapping = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<ChannelMappingData> channelmappingDatas = channelMappingReadPlatformService.retrieveChannelMapping(searchChannelMapping);
		return apiJsonSerializer.serialize(channelmappingDatas);
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createChannelMapping(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createChannelMapping().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
		
	}
	
	@GET
	@Path("{channelmappingId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveChannelMappingDetails(@Context final UriInfo uriInfo ,@PathParam("channelmappingId") final Long channelmappingId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final ChannelMappingData channelmappingDatas = this.channelMappingReadPlatformService.retrieveChannelMapping(channelmappingId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings,channelmappingDatas,RESPONSE_DATA_PARAMETERS);
		
	}
	
	@PUT
	@Path("{channelmappingId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateChannelMapping(@PathParam("channelmappingId") final Long channelmappingId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateChannelMapping(channelmappingId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}

}
