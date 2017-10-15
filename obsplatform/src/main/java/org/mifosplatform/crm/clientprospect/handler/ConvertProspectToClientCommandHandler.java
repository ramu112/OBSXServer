package org.mifosplatform.crm.clientprospect.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.clientprospect.service.ClientProspectWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConvertProspectToClientCommandHandler implements NewCommandSourceHandler {

	private final ClientProspectWritePlatformService prospectWritePlatformService;

	@Autowired
	public ConvertProspectToClientCommandHandler(final ClientProspectWritePlatformService prospectWritePlatformService) {
		this.prospectWritePlatformService = prospectWritePlatformService;

	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return prospectWritePlatformService.convertToClient(command.entityId());
	}

}
