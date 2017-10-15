package org.mifosplatform.portfolio.order.data;



import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;

public class UserActionStatusEnumaration {

	public static EnumOptionData OrderStatusType(final int id) {
		return OrderStatusType(UserActionStatusTypeEnum.fromInt(id));
	}

	public static EnumOptionData OrderStatusType(final UserActionStatusTypeEnum type) {
		final String codePrefix = "deposit.interest.compounding.period.";
		EnumOptionData optionData = null;
		switch (type) {
		case ACTIVATION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.ACTIVATION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.ACTIVATION  .getCode(), "ACTIVATION");
			break;
		case RECONNECTION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.RECONNECTION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.RECONNECTION.getCode(), "RECONNECTION");
			break;

		case DISCONNECTION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.DISCONNECTION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.DISCONNECTION.getCode(), "DISCONNECTION");
			break;
			
		case RENEWAL_AFTER_AUTOEXIPIRY:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.RENEWAL_AFTER_AUTOEXIPIRY.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.RENEWAL_AFTER_AUTOEXIPIRY.getCode(), "RENEWAL AFTER AUTOEXIPIRY");
			break;
			
		case RENEWAL_BEFORE_AUTOEXIPIRY:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY.getCode(), "RENEWAL BEFORE AUTOEXIPIRY");
			break;		
	
		case DEVICE_SWAP:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.DEVICE_SWAP.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.DEVICE_SWAP.getCode(), "DEVICE SWAP");
			break;	
		
		case CHANGE_PLAN:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.CHANGE_PLAN.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.CHANGE_PLAN.getCode(), "CHANGE PLAN");
			break;
			
		case CHANGE_GROUP:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.CHANGE_GROUP.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.CHANGE_GROUP.getCode(), "CHANGE GROUP"); 
			break;
			
		case TERMINATION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.TERMINATION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.TERMINATION.getCode(), "TERMINATION"); 
			break;
		case REACTIVATION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.REACTIVATION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.REACTIVATION.getCode(), "REACTIVATION"); 
			break;	
			
		case ADDON_ACTIVATION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.ADDON_ACTIVATION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.ADDON_ACTIVATION.getCode(), "ADDON_ACTIVATION"); 
			break;		
		case ADDON_DISCONNECTION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.ADDON_ACTIVATION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.ADDON_DISCONNECTION.getCode(), "ADDON_DISCONNECTION"); 
			break;
		default:
			optionData = new EnumOptionData(StatusTypeEnum.INVALID.getValue().longValue(), StatusTypeEnum.INVALID.getCode(), "INVALID");
			break;
		}
		return optionData;
	}

}
