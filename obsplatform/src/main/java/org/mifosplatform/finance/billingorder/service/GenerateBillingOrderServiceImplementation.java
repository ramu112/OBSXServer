package org.mifosplatform.finance.billingorder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.BillingOrder;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.billingorder.domain.InvoiceRepository;
import org.mifosplatform.finance.billingorder.domain.InvoiceTax;
import org.mifosplatform.finance.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenerateBillingOrderServiceImplementation implements GenerateBillingOrderService {

	private final GenerateBill generateBill;
	private final BillingOrderReadPlatformService billingOrderReadPlatformService;
	private final InvoiceRepository invoiceRepository;
	

	@Autowired
	public GenerateBillingOrderServiceImplementation(final GenerateBill generateBill,final BillingOrderReadPlatformService billingOrderReadPlatformService,
			final InvoiceRepository invoiceRepository) {
	
		this.generateBill = generateBill;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.invoiceRepository = invoiceRepository;
	
	}

	@Override
	public List<BillingOrderCommand> generatebillingOrder(List<BillingOrderData> products) {

		BillingOrderCommand billingOrderCommand = null;
		List<BillingOrderCommand> billingOrderCommands = new ArrayList<BillingOrderCommand>();

		if (products.size() != 0) {

			for (BillingOrderData billingOrderData : products) {
				// discount master
				DiscountMasterData discountMasterData = null;
				List<DiscountMasterData> discountMasterDatas = billingOrderReadPlatformService.retrieveDiscountOrders(billingOrderData.getClientOrderId(),
						billingOrderData.getOderPriceId());

				if (discountMasterDatas.size() != 0) {
					discountMasterData = discountMasterDatas.get(0);
				}

				if (billingOrderData.getOrderStatus() == 3) {
					billingOrderCommand = generateBill.getCancelledOrderBill(billingOrderData, discountMasterData);
					billingOrderCommands.add(billingOrderCommand);
				}

				else if (generateBill.isChargeTypeNRC(billingOrderData)) {

					System.out.println("---- NRC ---");
					billingOrderCommand = generateBill.getOneTimeBill(billingOrderData, discountMasterData);
					billingOrderCommands.add(billingOrderCommand);

				} else if (generateBill.isChargeTypeRC(billingOrderData)){
					
				 	 System.out.println("---- RC ---");
					if (billingOrderData.getDurationType().equalsIgnoreCase("month(s)")) {
						 if (billingOrderData.getBillingAlign().equalsIgnoreCase("N")) {

							billingOrderCommand = generateBill.getMonthyBill(billingOrderData, discountMasterData);
							billingOrderCommands.add(billingOrderCommand);
							

						} else if (billingOrderData.getBillingAlign().equalsIgnoreCase("Y")) {

							if (billingOrderData.getInvoiceTillDate() == null) {

								billingOrderCommand = generateBill.getProrataMonthlyFirstBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getInvoiceTillDate() != null) {

								billingOrderCommand = generateBill.getNextMonthBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);
							}
						}

						// weekly
					} else if (billingOrderData.getDurationType().equalsIgnoreCase("week(s)")) {

						if (billingOrderData.getBillingAlign().equalsIgnoreCase("N")) {
							
							billingOrderCommand = generateBill.getWeeklyBill(billingOrderData, discountMasterData);
							billingOrderCommands.add(billingOrderCommand);

						} else if (billingOrderData.getBillingAlign().equalsIgnoreCase("Y")) {

							if (billingOrderData.getInvoiceTillDate() == null) {

								billingOrderCommand = generateBill.getProrataWeeklyFirstBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getInvoiceTillDate() != null) {

								billingOrderCommand = generateBill.getNextWeeklyBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);
							}
						}

						// daily
					} else if (billingOrderData.getDurationType().equalsIgnoreCase("Day(s)")) {

						billingOrderCommand = generateBill.getDailyBill(billingOrderData, discountMasterData);
						billingOrderCommands.add(billingOrderCommand);

					}
				}/*else if(generateBill.isChargeTypeUC(billingOrderData)){
					
					System.out.println("---- UC ---");
				}*/

			}
		} else {
			throw new BillingOrderNoRecordsFoundException();
		}
		
		return billingOrderCommands;
	}

	@Transactional
	@Override
	public Invoice generateInvoice(List<BillingOrderCommand> billingOrderCommands) {

		BigDecimal invoiceAmount = BigDecimal.ZERO;
		BigDecimal totalChargeAmount = BigDecimal.ZERO;
		BigDecimal netTaxAmount = BigDecimal.ZERO;

		Invoice invoice = new Invoice(billingOrderCommands.get(0).getClientId(),DateUtils.getLocalDateOfTenant().toDate(), 
				                      invoiceAmount, invoiceAmount,netTaxAmount, "active");

		for (BillingOrderCommand billingOrderCommand : billingOrderCommands) {
			
			BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
			BigDecimal discountAmount = BigDecimal.ZERO;
			BigDecimal netChargeAmount = billingOrderCommand.getPrice();
			String discountCode="None";
			
			
			if (billingOrderCommand.getDiscountMasterData() != null) {
				discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
				 discountCode = billingOrderCommand.getDiscountMasterData().getDiscountCode(); 
			    netChargeAmount = billingOrderCommand.getPrice().subtract(discountAmount);

			}

			List<InvoiceTaxCommand> invoiceTaxCommands = billingOrderCommand.getListOfTax();

			BillingOrder charge = new BillingOrder(billingOrderCommand.getClientId(),billingOrderCommand.getClientOrderId(),
					billingOrderCommand.getOrderPriceId(),billingOrderCommand.getChargeCode(),billingOrderCommand.getChargeType(),
					discountCode,billingOrderCommand.getPrice(), discountAmount,netChargeAmount, billingOrderCommand.getStartDate(),
					billingOrderCommand.getEndDate());

			if (!invoiceTaxCommands.isEmpty()) {

				for (InvoiceTaxCommand invoiceTaxCommand : invoiceTaxCommands) {

					if (BigDecimal.ZERO.compareTo(invoiceTaxCommand.getTaxAmount()) < 0) {
						
						netChargeTaxAmount = netChargeTaxAmount.add(invoiceTaxCommand.getTaxAmount());
						InvoiceTax invoiceTax = new InvoiceTax(invoice, charge,invoiceTaxCommand.getTaxCode(),
								invoiceTaxCommand.getTaxValue(),invoiceTaxCommand.getTaxPercentage(),invoiceTaxCommand.getTaxAmount());
						charge.addChargeTaxes(invoiceTax);
					}
				}

				if (billingOrderCommand.getTaxInclusive() != null){
					if (isTaxInclusive(billingOrderCommand.getTaxInclusive())&&invoiceTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
						netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
						charge.setNetChargeAmount(netChargeAmount);
						charge.setChargeAmount(netChargeAmount);
					}
				  }
			}
			
			netTaxAmount = netTaxAmount.add(netChargeTaxAmount);
			totalChargeAmount = totalChargeAmount.add(netChargeAmount);
			invoice.addCharges(charge);
		}

		invoiceAmount = totalChargeAmount.add(netTaxAmount);
		invoice.setNetChargeAmount(totalChargeAmount);
		invoice.setTaxAmount(netTaxAmount);
		invoice.setInvoiceAmount(invoiceAmount);
		return this.invoiceRepository.saveAndFlush(invoice);
	}

	public BigDecimal getInvoiceAmount(List<BillingOrderCommand> billingOrderCommands) {
		
		BigDecimal invoiceAmount = BigDecimal.ZERO;
		for (BillingOrderCommand billingOrderCommand : billingOrderCommands) {
			invoiceAmount = invoiceAmount.add(billingOrderCommand.getPrice());
		}
		return invoiceAmount;
	}

	public Boolean isTaxInclusive(Integer taxInclusive) {

		Boolean isTaxInclusive = false;
		if (taxInclusive == 1)
			isTaxInclusive = true;

		return isTaxInclusive;
	}

	@Override
	public Invoice generateMultiOrderInvoice(List<BillingOrderCommand> billingOrderCommands, Invoice newInvoice) {

		BigDecimal invoiceAmount = BigDecimal.ZERO;
		BigDecimal totalChargeAmount = BigDecimal.ZERO;
		BigDecimal netTaxAmount = BigDecimal.ZERO;
		Invoice invoice=null;
		
		if (newInvoice != null) {
           
			invoice=newInvoice;
			
		} else {
			invoice = new Invoice(billingOrderCommands.get(0).getClientId(),DateUtils.getLocalDateOfTenant().toDate(), 
					  invoiceAmount,invoiceAmount, netTaxAmount, "active");
		}
		
		for (BillingOrderCommand billingOrderCommand : billingOrderCommands) {

			BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
			BigDecimal discountAmount = BigDecimal.ZERO;
			BigDecimal netChargeAmount = billingOrderCommand.getPrice();
			String discountCode = "None";

			if (billingOrderCommand.getDiscountMasterData() != null) {
				discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
				discountCode =billingOrderCommand.getDiscountMasterData().getDiscountCode();
				netChargeAmount = billingOrderCommand.getPrice().subtract(discountAmount);

			}

			List<InvoiceTaxCommand> invoiceTaxCommands = billingOrderCommand.getListOfTax();

			BillingOrder charge = new BillingOrder(billingOrderCommand.getClientId(),billingOrderCommand.getClientOrderId(),
					billingOrderCommand.getOrderPriceId(),billingOrderCommand.getChargeCode(),
					billingOrderCommand.getChargeType(), discountCode,billingOrderCommand.getPrice(), discountAmount,
					netChargeAmount, billingOrderCommand.getStartDate(),billingOrderCommand.getEndDate());

			if (!invoiceTaxCommands.isEmpty()) {

					for (InvoiceTaxCommand invoiceTaxCommand : invoiceTaxCommands) {

						if (BigDecimal.ZERO.compareTo(invoiceTaxCommand.getTaxAmount()) < 0) {

							netChargeTaxAmount = netChargeTaxAmount.add(invoiceTaxCommand.getTaxAmount());
							InvoiceTax invoiceTax = new InvoiceTax(invoice,charge, invoiceTaxCommand.getTaxCode(),
									invoiceTaxCommand.getTaxValue(),invoiceTaxCommand.getTaxPercentage(),invoiceTaxCommand.getTaxAmount());
							charge.addChargeTaxes(invoiceTax);
						}
				}

				if (billingOrderCommand.getTaxInclusive() != null){
					if (isTaxInclusive(billingOrderCommand.getTaxInclusive())&& invoiceTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
						netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
						charge.setNetChargeAmount(netChargeAmount);
						charge.setChargeAmount(netChargeAmount);
					}
				}
			}
			netTaxAmount = netTaxAmount.add(netChargeTaxAmount);
			totalChargeAmount = totalChargeAmount.add(netChargeAmount);
			invoice.addCharges(charge);

		}

		invoiceAmount = totalChargeAmount.add(netTaxAmount);
		invoice.setNetChargeAmount(totalChargeAmount.add(invoice.getNetChargeAmount()));
		invoice.setTaxAmount(netTaxAmount.add(invoice.getTaxAmount()));
		invoice.setInvoiceAmount(invoiceAmount.add(invoice.getInvoiceAmount()));
		return invoice;

	}

}
