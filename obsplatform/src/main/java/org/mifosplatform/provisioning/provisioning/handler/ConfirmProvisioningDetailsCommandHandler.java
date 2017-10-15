package org.mifosplatform.provisioning.provisioning.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmProvisioningDetailsCommandHandler implements
		NewCommandSourceHandler {

	private final ProvisioningWritePlatformService writePlatformService;

	@Autowired
	public ConfirmProvisioningDetailsCommandHandler(
			final ProvisioningWritePlatformService writePlatformService) {
		this.writePlatformService = writePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.writePlatformService.confirmProvisioningDetails(command
				.entityId());
	}
}
