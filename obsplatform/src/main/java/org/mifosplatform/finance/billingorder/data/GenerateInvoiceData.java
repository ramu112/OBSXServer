package org.mifosplatform.finance.billingorder.data;

import java.math.BigDecimal;
import java.util.Date;

import org.mifosplatform.finance.billingorder.domain.Invoice;

public class GenerateInvoiceData {
	
	private final Long clientId;
	private final Date nextBillableDay;
	private BigDecimal invoiceAmount;
	private Invoice invoice;
	
	public GenerateInvoiceData( final Long clientId, final Date nextBillableDay,BigDecimal invoiceAmount,Invoice invoice) {
		this.clientId = clientId;
		this.nextBillableDay = nextBillableDay;
		this.invoiceAmount=invoiceAmount;
		this.invoice = invoice;
	}

	public Long getClientId() {
		return clientId;
	}

	public Date getNextBillableDay() {
		return nextBillableDay;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public Invoice getInvoice() {
		return invoice;
	}

}
