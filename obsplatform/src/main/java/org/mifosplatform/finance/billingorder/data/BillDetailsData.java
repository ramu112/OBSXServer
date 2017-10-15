package org.mifosplatform.finance.billingorder.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;


public class BillDetailsData  {
	
private Long id;
private Long clientId;
private String billPeriod;
private Double previousBalance;
private Double chargeAmount;
private Double adjustmentAmount;
private Double taxAmount;
private Double paidAmount;
private BigDecimal dueAmount;
private LocalDate billDate;
private LocalDate dueDate;
private String promotionalMessage;
private String billNo;
private String date;
private String transaction;
private BigDecimal amount;
private String payments;

	public BillDetailsData(Long id, Long clientId, LocalDate dueDate,
			String transactionType, BigDecimal dueAmount, BigDecimal amount,
			LocalDate transDate) {

		this.id = id;
		this.dueDate = dueDate;
		this.transaction = transactionType;
		this.dueAmount = dueAmount;
		this.amount = amount;
		this.clientId = clientId;

	}

	public BillDetailsData(Long id, LocalDate billDate, LocalDate dueDate,
			BigDecimal amount) {

		this.id = id;
		this.billDate = billDate;
		this.dueDate = dueDate;
		this.amount = amount;

	}

	public Long getId() {
		return id;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getBillPeriod() {
		return billPeriod;
	}

	public Double getPreviousBalance() {
		return previousBalance;
	}

	public String getPromotionalMessage() {
		return promotionalMessage;
	}

	public String getBillNo() {
		return billNo;
	}

	public String getDate() {
		return date;
	}

	public String getTransaction() {
		return transaction;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getPayments() {
		return payments;
	}

	public Double getChargeAmount() {
		return chargeAmount;
	}

	public Double getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public Double getTaxAmount() {
		return taxAmount;
	}

	public Double getPaidAmount() {
		return paidAmount;
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public LocalDate getBillDate() {
		return billDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public String getMessage() {
		return promotionalMessage;
	}

}
