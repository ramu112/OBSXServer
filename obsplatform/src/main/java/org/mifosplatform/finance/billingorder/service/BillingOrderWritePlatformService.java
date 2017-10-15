package org.mifosplatform.finance.billingorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface BillingOrderWritePlatformService {

	
	CommandProcessingResult updateBillingOrder(List<BillingOrderCommand> billingOrderCommands);
	
	void updateClientBalance(BigDecimal value,Long clientId, boolean isWalletEnable);
	
	void updateClientVoucherBalance(BigDecimal amount,Long clientId, boolean isWalletEnable);

	void UpdateOfficeCommision(Invoice invoice, Long agreementId);
	
}
