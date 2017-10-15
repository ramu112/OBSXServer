package org.mifosplatform.logistics.item.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ItemNotFoundException extends AbstractPlatformResourceNotFoundException {

public ItemNotFoundException(String itemId) {

super("error.msg.item.id.not.found","Item is Not Found",itemId);

}

}
