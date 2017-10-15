
package org.mifosplatform.logistics.onetimesale.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelOneTimeSaleCommandHandler implements NewCommandSourceHandler {

    private final OneTimeSaleWritePlatformService writePlatformService;

    @Autowired
    public CancelOneTimeSaleCommandHandler(final OneTimeSaleWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.deleteOneTimeSale(command.entityId());
    }
}