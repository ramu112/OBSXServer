/**
 * 
 */
package org.mifosplatform.cms.eventmaster.handler;

import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.cms.eventmaster.service.EventMasterWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link Service} Class for updating {@link EventMaster}
 * implements {@link NewCommandSourceHandler}
 * 
 * @author pavani
 * @author Rakesh
 */
@Service
public class UpdateEventMasterCommandHandler implements NewCommandSourceHandler {

	@Autowired
	private EventMasterWritePlatformService eventMasterWritePlatformService;

	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		return this.eventMasterWritePlatformService.updateEventMaster(command);
	}

}
