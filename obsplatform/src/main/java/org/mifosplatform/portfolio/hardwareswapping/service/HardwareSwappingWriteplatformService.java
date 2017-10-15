package org.mifosplatform.portfolio.hardwareswapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface HardwareSwappingWriteplatformService {

	CommandProcessingResult doHardWareSwapping(Long entityId,JsonCommand command);

}
