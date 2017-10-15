package org.mifosplatform.portfolio.service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.service.domain.ServiceDetails;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterDetailsRepository;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.portfolio.service.serialization.ServiceCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Service
public class ServiceMasterWritePlatformServiceImpl  implements ServiceMasterWritePlatformService{
	
	private final static Logger logger = LoggerFactory.getLogger(ServiceMasterWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final ServiceMasterRepository serviceMasterRepository;
	private final ServiceCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	private final ServiceMasterDetailsRepository serviceMasterDetailsRepository;
	
	@Autowired
 public ServiceMasterWritePlatformServiceImpl(final PlatformSecurityContext context,final ServiceMasterRepository serviceMasterRepository,
		 final ServiceCommandFromApiJsonDeserializer fromApiJsonDeserializer,final FromJsonHelper fromApiJsonHelper,
		 final ServiceMasterDetailsRepository serviceMasterDetailsRepository)
  {
		
	this.context=context;
	this.serviceMasterRepository=serviceMasterRepository;
	this.fromApiJsonDeserializer=fromApiJsonDeserializer;
	this.fromApiJsonHelper = fromApiJsonHelper;
	this.serviceMasterDetailsRepository = serviceMasterDetailsRepository;
}
    @Transactional
	@Override
	public CommandProcessingResult createNewService(final JsonCommand command) {
		try {
			   this.context.authenticatedUser();
			   this.fromApiJsonDeserializer.validateForCreate(command.json());
			   ServiceMaster serviceMaster = ServiceMaster.fromJson(command);
			   final JsonArray serviceChildArray = command.arrayOfParameterNamed("serviceArray").getAsJsonArray();
			   serviceMaster= this.assembleDetails(serviceChildArray,serviceMaster);
			   this.serviceMasterRepository.saveAndFlush(serviceMaster);
			   return new CommandProcessingResult(serviceMaster.getId());
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return  CommandProcessingResult.empty();
		}
	}
    
    private ServiceMaster assembleDetails(JsonArray serviceChildArray, ServiceMaster serviceMaster) {
		
		String[]  childServices = null;
		childServices = new String[serviceChildArray.size()];
		if(serviceChildArray.size() > 0){
			for(int i = 0; i < serviceChildArray.size(); i++){
				childServices[i] = serviceChildArray.get(i).toString();
			}
	
		for (final String childService : childServices) {
			final JsonElement element = fromApiJsonHelper.parse(childService);
			final String paramName = fromApiJsonHelper.extractStringNamed("paramName", element);
			final String paramType = fromApiJsonHelper.extractStringNamed("paramType", element);
			final String paramValue = fromApiJsonHelper.extractStringNamed("paramValue", element);
			ServiceDetails serviceDetails = new ServiceDetails(paramName, paramType,paramValue);
			serviceMaster.addDetails(serviceDetails);
			
		}	 
	}	
	
	return serviceMaster;
}
    
	@Override
	public CommandProcessingResult updateService(final Long serviceId,final JsonCommand command) {
		
		try{
			    this.context.authenticatedUser();
			    this.fromApiJsonDeserializer.validateForCreate(command.json());
		        final ServiceMaster serviceMaster = retrieveCodeBy(serviceId);
		        List<ServiceDetails> details=new ArrayList<>(serviceMaster.getServiceDetails());
		        final JsonArray serviceChildArray = command.arrayOfParameterNamed("serviceArray").getAsJsonArray();
		        String[] service =null;
		        service=new String[serviceChildArray.size()];
			    for(int i=0; i<serviceChildArray.size();i++){
			    	service[i] =serviceChildArray.get(i).toString();
			     }
				 for (String serviceData : service) {
					  
					    final JsonElement element = fromApiJsonHelper.parse(serviceData);
					    final Long id = fromApiJsonHelper.extractLongNamed("id", element);
						final String paramName = fromApiJsonHelper.extractStringNamed("paramName", element);
						final String paramType = fromApiJsonHelper.extractStringNamed("paramType", element);
						final String paramValue = fromApiJsonHelper.extractStringNamed("paramValue", element);
						if(id != null){
						ServiceDetails serviceDetails =this.serviceMasterDetailsRepository.findOne(id);
						
						if(serviceDetails != null){
							serviceDetails.setParamName(paramName);
							serviceDetails.setParamType(paramType);
							serviceDetails.setParamValue(paramValue);
							this.serviceMasterDetailsRepository.saveAndFlush(serviceDetails);
							if(details.contains(serviceDetails)){
							   details.remove(serviceDetails);
							}
						}
						}else {
							ServiceDetails newDetails = new ServiceDetails(paramName, paramType,paramValue);
							serviceMaster.addDetails(newDetails);
						}
						
				  }
				 serviceMaster.getServiceDetails().removeAll(details);
				 
		        final Map<String, Object> changes = serviceMaster.update(command);
	            if (!changes.isEmpty()) {
	                this.serviceMasterRepository.saveAndFlush(serviceMaster);
	            }
	            
         return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(serviceId).with(changes).build();
         
	} catch (DataIntegrityViolationException dve) {
		 handleCodeDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	}
	}
	 private void handleCodeDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
	        final Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("service_code_key")) {
	            final String name = command.stringValueOfParameterNamed("serviceCode");
	            throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name'"
	                    + name + "'already exists", "displayName", name);
	        }

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
	    }
	private ServiceMaster retrieveCodeBy(final Long serviceId) {
	        final ServiceMaster serviceMaster = this.serviceMasterRepository.findOne(serviceId);
	        if (serviceMaster == null) { throw new CodeNotFoundException(serviceId.toString()); }
	        return serviceMaster;
	    }
	 
	 
	@Override
	public CommandProcessingResult deleteService(final Long serviceId) {
				
		    this.context.authenticatedUser();
	        final ServiceMaster serviceMaster = retrieveCodeBy(serviceId);
	        serviceMaster.delete();
			this.serviceMasterRepository.save(serviceMaster);
	        return new CommandProcessingResultBuilder().withEntityId(serviceId).build();
	    }

	}




