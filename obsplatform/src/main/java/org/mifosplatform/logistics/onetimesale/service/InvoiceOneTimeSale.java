package org.mifosplatform.logistics.onetimesale.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeMaster;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.billing.discountmaster.domain.DiscountDetails;
import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.mifosplatform.billing.taxmaster.data.TaxMappingRateData;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.billingorder.service.BillingOrderReadPlatformService;
import org.mifosplatform.finance.billingorder.service.BillingOrderWritePlatformService;
import org.mifosplatform.finance.billingorder.service.GenerateBill;
import org.mifosplatform.finance.billingorder.service.GenerateBillingOrderService;
import org.mifosplatform.finance.billingorder.service.GenerateDisconnectionBill;
import org.mifosplatform.finance.billingorder.service.GenerateReverseBillingOrderService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ranjith
 * invoices for device sale and additional fee charges
 */
@Service
public class InvoiceOneTimeSale {

	private final GenerateBill generateBill;
	private final BillingOrderWritePlatformService billingOrderWritePlatformService;
	private final GenerateBillingOrderService generateBillingOrderService;
	private final GenerateDisconnectionBill generateDisconnectionBill;
	private final GenerateReverseBillingOrderService generateReverseBillingOrderService;
	private final DiscountMasterRepository discountMasterRepository;
	private final BillingOrderReadPlatformService billingOrderReadPlatformService;
	private final ClientRepositoryWrapper clientRepository;
	
	@Autowired
	public InvoiceOneTimeSale(final GenerateBill generateBill,final BillingOrderWritePlatformService billingOrderWritePlatformService,
			final GenerateBillingOrderService generateBillingOrderService,final GenerateDisconnectionBill generateDisconnectionBill,
			final GenerateReverseBillingOrderService generateReverseBillingOrderService,final DiscountMasterRepository discountMasterRepository,
			final ClientRepositoryWrapper clientRepository,final BillingOrderReadPlatformService billingOrderReadPlatformService) {
		
		this.generateBill = generateBill;
		this.billingOrderWritePlatformService = billingOrderWritePlatformService;
		this.generateBillingOrderService = generateBillingOrderService;
		this.generateDisconnectionBill = generateDisconnectionBill;
		this.generateReverseBillingOrderService = generateReverseBillingOrderService;
		this.discountMasterRepository = discountMasterRepository;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.clientRepository = clientRepository;


	}

/**
 * @param clientId
 * @param oneTimeSaleData
 * @param Wallet flag
 */
	public CommandProcessingResult invoiceOneTimeSale(final Long clientId,final OneTimeSaleData oneTimeSaleData, boolean isWalletEnable) {

		BigDecimal discountRate = BigDecimal.ZERO;
		
		List<BillingOrderCommand> billingOrderCommands = new ArrayList<BillingOrderCommand>();

		BillingOrderData billingOrderData = new BillingOrderData(oneTimeSaleData.getId(), oneTimeSaleData.getClientId(),DateUtils.getLocalDateOfTenant().toDate(),
				oneTimeSaleData.getChargeCode(),oneTimeSaleData.getChargeType(),oneTimeSaleData.getTotalPrice(),oneTimeSaleData.getTaxInclusive());

		Client client=this.clientRepository.findOneWithNotFoundDetection(clientId);
		
		DiscountMaster discountMaster=this.discountMasterRepository.findOne(oneTimeSaleData.getDiscountId());
		
		List<DiscountDetails> discountDetails=discountMaster.getDiscountDetails();
		for(DiscountDetails discountDetail:discountDetails){
			if(client.getCategoryType().equals(Long.valueOf(discountDetail.getCategoryType()))){
				discountRate = discountDetail.getDiscountRate();
			}else if(discountRate.equals(BigDecimal.ZERO) && Long.valueOf(discountDetail.getCategoryType()).equals(Long.valueOf(0))){
				discountRate = discountDetail.getDiscountRate();
			}
		}

		DiscountMasterData discountMasterData = new DiscountMasterData(discountMaster.getId(), discountMaster.getDiscountCode(),discountMaster.getDiscountDescription(),
				discountMaster.getDiscountType(),discountRate, null, null);
		
			discountMasterData = this.calculateDiscount(discountMasterData,billingOrderData.getPrice());

		BillingOrderCommand billingOrderCommand = this.generateBill.getOneTimeBill(billingOrderData, discountMasterData);

		billingOrderCommands.add(billingOrderCommand);

		// calculation of invoice
		Invoice invoice = this.generateBillingOrderService.generateInvoice(billingOrderCommands);

		// Update Client Balance
		this.billingOrderWritePlatformService.updateClientBalance(invoice.getInvoiceAmount(), clientId, isWalletEnable);

		return new CommandProcessingResult(invoice.getId());

	}

/**
 * @param clientId
 * @param oneTimeSaleData
 * @param invoice 
 * @param wallet 
 *  reverse invoice 
 */
	public CommandProcessingResult reverseInvoiceForOneTimeSale(final Long clientId, final OneTimeSaleData oneTimeSaleData,final BigDecimal discountAmount,final boolean isWalletEnable) {
        
		
		BigDecimal discountRate = BigDecimal.ZERO;
		
		List<BillingOrderCommand> billingOrderCommands = new ArrayList<BillingOrderCommand>();

		BillingOrderData billingOrderData = new BillingOrderData(oneTimeSaleData.getId(), clientId, DateUtils.getLocalDateOfTenant().toDate(),
				oneTimeSaleData.getChargeCode(),oneTimeSaleData.getChargeType(),oneTimeSaleData.getTotalPrice(),oneTimeSaleData.getTaxInclusive());
		

		DiscountMaster discountMaster=this.discountMasterRepository.findOne(oneTimeSaleData.getDiscountId());
		
		DiscountMasterData discountMasterData = new DiscountMasterData(discountMaster.getId(), discountMaster.getDiscountCode(),discountMaster.getDiscountDescription(),
				discountMaster.getDiscountType(),discountRate, null, null,discountAmount);

		BillingOrderCommand billingOrderCommand = this.generateDisconnectionBill.getReverseOneTimeBill(billingOrderData, discountMasterData);
		
		 billingOrderCommands.add(billingOrderCommand);

		// calculation of reverse invoice
		Invoice invoice = this.generateReverseBillingOrderService.generateNegativeInvoice(billingOrderCommands);

		// To fetch record from client_balance table
		this.billingOrderWritePlatformService.updateClientBalance(invoice.getInvoiceAmount(),clientId,isWalletEnable);

		return new CommandProcessingResult(invoice.getId());

	}
	

	/**
	 * @param chargeMaster
	 * @param orderId
	 * @param priceId
	 * @param clientId
	 * @param feeChargeAmount
	 * @return invoice
	 */
	public Invoice calculateAdditionalFeeCharges(final ChargeCodeMaster chargeMaster,final Long orderId, final Long priceId, 
			                      final Long clientId, final BigDecimal ChargeAmount) {
		
		List<BillingOrderCommand> billingOrderCommands = new ArrayList<BillingOrderCommand>();
		List<InvoiceTaxCommand>  listOfTaxes = this.calculateTax(clientId, ChargeAmount,chargeMaster);
		BillingOrderCommand billingOrderCommand = new BillingOrderCommand(orderId,priceId,clientId, DateUtils.getDateOfTenant(),
				DateUtils.getDateOfTenant(),DateUtils.getDateOfTenant(),chargeMaster.getBillFrequencyCode(), chargeMaster.getChargeCode(),
				chargeMaster.getChargeType(),chargeMaster.getChargeDuration(), "",DateUtils.getDateOfTenant(),ChargeAmount, 
				"N",listOfTaxes, DateUtils.getDateOfTenant(),DateUtils.getDateOfTenant(), null,chargeMaster.getTaxInclusive());

		billingOrderCommands.add(billingOrderCommand);
		
		Invoice invoice = this.generateBillingOrderService.generateInvoice(billingOrderCommands);
		
		this.billingOrderWritePlatformService.updateClientBalance(invoice.getInvoiceAmount(), clientId, false);
	
		return invoice;
	}
	
	/**
	 * @param clientId
	 * @param chargeAmount
	 * @param chargeMaster
	 * @return
	 */
	public List<InvoiceTaxCommand> calculateTax(Long clientId,BigDecimal billPrice, ChargeCodeMaster chargeMaster) {

		// Get State level taxes
		List<TaxMappingRateData> taxMappingRateDatas = this.billingOrderReadPlatformService.retrieveTaxMappingData(clientId,chargeMaster.getChargeCode());
		if (taxMappingRateDatas.isEmpty()) {
			taxMappingRateDatas = this.billingOrderReadPlatformService.retrieveDefaultTaxMappingData(clientId,chargeMaster.getChargeCode());
		}
		List<InvoiceTaxCommand> invoiceTaxCommand = this.generateBill.generateInvoiceTax(taxMappingRateDatas, billPrice, clientId,chargeMaster.getTaxInclusive());
		
		return invoiceTaxCommand;

	}

	// Discount Applicable Logic
	public boolean isDiscountApplicable(final DiscountMasterData discountMasterData) {
		boolean isDiscountApplicable = true;
		
		return isDiscountApplicable;

	}

	// Discount End Date calculation if null
	public Date getDiscountEndDateIfNull(final DiscountMasterData discountMasterData) {
		LocalDate discountEndDate = discountMasterData.getDiscountEndDate();
		if (discountMasterData.getDiscountEndDate() == null) {
			discountEndDate = new LocalDate(2099, 0, 01);
		}
		return discountEndDate.toDate();

	}
	
	// if is percentage
	public boolean isDiscountPercentage(final DiscountMasterData discountMasterData){
		boolean isDiscountPercentage = false;
		if(discountMasterData.getDiscountType().equalsIgnoreCase("percentage")){
																
			isDiscountPercentage = true;
		}
		return isDiscountPercentage;
	}
	
	// if is discount
	public boolean isDiscountFlat(final DiscountMasterData discountMasterData){
		boolean isDiscountFlat = false;
		if(discountMasterData.getDiscountType().equalsIgnoreCase("flat")){
			
			isDiscountFlat = true;
		}
		return isDiscountFlat;
	}
	

	// Discount calculation 
	public DiscountMasterData calculateDiscount(final DiscountMasterData discountMasterData, BigDecimal chargePrice){
		
		BigDecimal discountAmount=BigDecimal.ZERO;
		if(isDiscountPercentage(discountMasterData)){
			
			if(discountMasterData.getDiscountRate().compareTo(new BigDecimal(100)) ==-1 ||
			 discountMasterData.getDiscountRate().compareTo(new BigDecimal(100)) == 0){
				
			discountAmount = this.calculateDiscountPercentage(discountMasterData.getDiscountRate(), chargePrice);
			discountMasterData.setDiscountAmount(discountAmount);
			chargePrice = this.chargePriceNotLessThanZero(chargePrice, discountAmount);
			discountMasterData.setDiscountedChargeAmount(chargePrice);
			
			}
			
		}
		
		if(isDiscountFlat(discountMasterData)){
			
			BigDecimal netFlatAmount=this.calculateDiscountFlat(discountMasterData.getDiscountRate(), chargePrice);
			netFlatAmount=this.chargePriceNotLessThanZero(chargePrice, discountMasterData.getDiscountRate());
			discountMasterData.setDiscountedChargeAmount(netFlatAmount);
			discountAmount = chargePrice.subtract(netFlatAmount);
			discountMasterData.setDiscountAmount(discountAmount);
			
		}
		return discountMasterData;
	
	}
	
	// Discount Percent calculation
	public BigDecimal calculateDiscountPercentage(final BigDecimal discountRate,final BigDecimal chargePrice){
		
		return chargePrice.multiply(discountRate.divide(new BigDecimal(100))).setScale(Integer.parseInt(this.generateBill.roundingDecimal()), RoundingMode.HALF_UP);
	}
	
	// Discount Flat calculation
	public BigDecimal calculateDiscountFlat(final BigDecimal discountRate,final BigDecimal chargePrice){
		
		BigDecimal discountFlat=BigDecimal.ZERO;
		//check for charge price zero and discount rate greater than zero
		if(chargePrice.compareTo(BigDecimal.ZERO) == 1 ){
			discountFlat=chargePrice.subtract(discountRate).setScale(Integer.parseInt(this.generateBill.roundingDecimal()),RoundingMode.HALF_UP);
		}
		return discountFlat;
	}
	
	// to check price not less than zero
	public BigDecimal chargePriceNotLessThanZero(BigDecimal chargePrice,final BigDecimal discountPrice){
		
		chargePrice = chargePrice.subtract(discountPrice);
		if(chargePrice.compareTo(discountPrice) < 0){
			chargePrice = BigDecimal.ZERO;
		}
		return chargePrice;
		
	}

}
