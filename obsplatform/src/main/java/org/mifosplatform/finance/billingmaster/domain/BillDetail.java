package org.mifosplatform.finance.billingmaster.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "b_bill_details")
public class BillDetail {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@ManyToOne
    @JoinColumn(name = "bill_id")
    private BillMaster billMaster;

	@Column(name = "transaction_id")
	private  Long transactionId;

	@Column(name = "Transaction_date")
	private Date transactionDate;

	@Column(name = "Transaction_type")
	private String transactionType;

	@Column(name = "Amount")
	private BigDecimal amount;
	
	@Column(name = "plan_code")
	private String planCode;
	
	@Column(name = "description")
	private String description;
	

	protected BillDetail() {

	}

	public BillDetail(final BillMaster billId, final Long transactionId, final Date transactionDate, final String transactionType,
			final BigDecimal amount, final String planCode, final String description) {

		this.billMaster = billId;
		this.transactionId = transactionId;
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
		this.amount = amount;
		this.planCode = planCode;
		this.description = description;

	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public BillMaster getBillId() {
		return billMaster;
	}

	public void setBillId(final BillMaster billId) {
		this.billMaster = billId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(final Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final Long transactionId) {
		this.transactionId = transactionId;
	}

	public void updateBillMaster(final BillMaster billMaster) {
           this.billMaster=billMaster;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(final String planCode) {
		this.planCode = planCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}
	
}