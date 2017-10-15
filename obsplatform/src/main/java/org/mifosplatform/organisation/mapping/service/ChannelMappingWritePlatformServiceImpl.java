package org.mifosplatform.organisation.mapping.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.organisation.mapping.domain.ChannelMapping;
import org.mifosplatform.organisation.mapping.domain.ChannelMappingRepository;
import org.mifosplatform.organisation.mapping.serialization.ChannelMappingCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ChannelMappingWritePlatformServiceImpl implements ChannelMappingWritePlatformService {
    
	private final PlatformSecurityContext context;
	private final ChannelMappingCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ChannelMappingRepository channelmappingRepository;
	
	@Autowired
	public ChannelMappingWritePlatformServiceImpl(PlatformSecurityContext context,
			ChannelMappingCommandFromApiJsonDeserializer apiJsonDeserializer, ChannelMappingRepository channelmappingRepository) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.channelmappingRepository = channelmappingRepository;
	}


	@Override
	public CommandProcessingResult create(JsonCommand command) {
		try{
		
		context.authenticatedUser();
		apiJsonDeserializer.validateForCreate(command.json());
		ChannelMapping channelmapping = ChannelMapping.formJson(command);
		this.channelmappingRepository.save(channelmapping);
		return new CommandProcessingResultBuilder().withEntityId(channelmapping.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	
	}


	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {
    	final Throwable realCause = dve.getMostSpecificCause();
    	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
		
	}


	@Override
	public CommandProcessingResult updateChannelMapping(JsonCommand command, Long channelmappingId) {  
		
		try{
		   
		   this.context.authenticatedUser();
		   this.apiJsonDeserializer.validateForCreate(command.json());
		ChannelMapping channelmapping = this.retrieveChannelMapping(channelmappingId);
		   final Map<String, Object> changes = channelmapping.update(command);
		   if(!changes.isEmpty()){
			   this.channelmappingRepository.save(channelmapping);
		   }
		   return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(channelmappingId) //
	       .with(changes) //
	       .build();
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		      return new CommandProcessingResult(Long.valueOf(-1));
		 }
	}
	
	private ChannelMapping retrieveChannelMapping(final Long channelmappingId) {
		ChannelMapping channelmapping = this.channelmappingRepository.findOne(channelmappingId);
		if (channelmapping == null) { throw new ChannelNotFoundException(channelmappingId); }
		return channelmapping;
	}
	
}
