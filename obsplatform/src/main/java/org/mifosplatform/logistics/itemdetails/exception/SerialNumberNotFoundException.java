package org.mifosplatform.logistics.itemdetails.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SerialNumberNotFoundException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerialNumberNotFoundException(String SerialNumber) {		
		super("error.msg.itemdetails.serialnumber.not.found", "SerialNumber not Exist with this "+SerialNumber, SerialNumber);
	}

}
