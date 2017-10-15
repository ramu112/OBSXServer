package org.mifosplatform.portfolio.order.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchedulingOrderUpdationCommandHandler implements NewCommandSourceHandler {
	
	 private final OrderWritePlatformService writePlatformService;

	    @Autowired
	    public SchedulingOrderUpdationCommandHandler(final OrderWritePlatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	    }

	    @Transactional
	    @Override
	    public CommandProcessingResult processCommand(final JsonCommand command) {

	        return this.writePlatformService.scheduleOrderUpdation(command.entityId(),command);
	    }

}