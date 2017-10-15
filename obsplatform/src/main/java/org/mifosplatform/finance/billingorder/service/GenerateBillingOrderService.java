package org.mifosplatform.finance.billingorder.service;


import java.util.List;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.Invoice;

public interface GenerateBillingOrderService {

	List<BillingOrderCommand> generatebillingOrder(List<BillingOrderData> products);

	Invoice generateInvoice(List<BillingOrderCommand> billingOrderCommands);

	Invoice generateMultiOrderInvoice(List<BillingOrderCommand> billingOrderCommands, Invoice invoice);
	
}
