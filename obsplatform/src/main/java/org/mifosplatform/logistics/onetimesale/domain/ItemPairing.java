package org.mifosplatform.logistics.onetimesale.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_item_pairing")
public class ItemPairing extends AbstractAuditableCustom<AppUser, Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "pairing_date")
	private Date pairingDate;

	@Column(name = "unpairing_date")
	private Date unpairingDate;
	
	@Column(name = "status")
	private String status;

	@Column(name = "serial_no_1")
	private String serialNo1;

	@Column(name = "item_type_1")
	private String itemType1;

	@Column(name = "serial_no_2")
	private String serialNo2;
	
	@Column(name = "item_type_2")
	private String itemType2;

	@Column(name = "is_deleted", nullable = false)
	private char isDeleted = 'N';
	
	public ItemPairing(){}
	
	public ItemPairing(final Long clientId, final Long serviceId,final Date pairingDate,final Date unpairingDate,
			final String status, final String serialNo1,final String itemType1,
            final String serialNo2, final String itemType2) {

	this.clientId=clientId;
	this.serviceId=serviceId;
	this.pairingDate=pairingDate;
	this.unpairingDate=unpairingDate;
	this.status=status;
	this.serialNo1=serialNo1;
	this.itemType1=itemType1;
	this.serialNo2=serialNo2;
	this.itemType2=itemType2;
	
	}


	public static ItemPairing fromJson(final Long clientId, final JsonCommand command, final ItemMaster item) {
		
			final Long serviceId = command.longValueOfParameterNamed("serviceId");
		    final Date pairingDate = command.DateValueOfParameterNamed("pairingDate");
		    final Date unpairingDate = command.DateValueOfParameterNamed("unpairingDate");
		    final String status=command.stringValueOfParameterNamed("status");
		    final String serialNo1=command.stringValueOfParameterNamed("serialNo1");
		    final String itemType1=command.stringValueOfParameterNamed("itemType1");
		    final String serialNo2=command.stringValueOfParameterNamed("serialNo2");
		    final String itemType2=command.stringValueOfParameterNamed("itemType2");
		    
          return new ItemPairing(clientId, serviceId,pairingDate,unpairingDate,status,serialNo1,itemType1,serialNo2, itemType2);
	}

}
