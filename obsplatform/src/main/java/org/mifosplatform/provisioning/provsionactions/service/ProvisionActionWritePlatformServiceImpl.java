package org.mifosplatform.provisioning.provsionactions.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisionActions;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisioningActionsRepository;
import org.mifosplatform.provisioning.provsionactions.exception.ProvisionActionsNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProvisionActionWritePlatformServiceImpl implements ProvisionActionWritePlatformService{
	
	private final PlatformSecurityContext context;
	private final ProvisioningActionsRepository provisioningActionsRepository;
	
@Autowired
public ProvisionActionWritePlatformServiceImpl(final PlatformSecurityContext context,final ProvisioningActionsRepository actionsRepository){
	
	this.context=context;
	this.provisioningActionsRepository=actionsRepository;
}

@Transactional	
@Override
public CommandProcessingResult updateProvisionActionStatus(JsonCommand command) {
	try{
		this.context.authenticatedUser();
		final ProvisionActions provisionActions=retrieveObjectById(command.entityId());
		final boolean status=command.booleanPrimitiveValueOfParameterNamed("status");
		provisionActions.updateStatus(status);
		this.provisioningActionsRepository.save(provisionActions);
		return new CommandProcessingResult(command.entityId());

	}catch(DataIntegrityViolationException dve){
    	 handleCodeDataIntegrityIssues(command,dve);
    	 return new CommandProcessingResult(Long.valueOf(-1));
	}
}

private ProvisionActions retrieveObjectById(Long entityId) {
	
	final ProvisionActions provisionActions=this.provisioningActionsRepository.findOne(entityId);
	if(provisionActions == null){throw new ProvisionActionsNotFoundException(entityId.toString());}
	return provisionActions;
}



	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub
		
	}

}
