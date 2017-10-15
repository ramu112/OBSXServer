package org.mifosplatform.organisation.mapping.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ChannelMappingNotFoundEXception extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	public ChannelMappingNotFoundEXception(Long id) {
		
		super("error.msg.channelmapping.id.not.found","channelmapping is Not Found",id);
	}

}
