package org.mifosplatform.logistics.item.domain;



import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.logistics.item.domain.ItemEnumType;

public class ItemTypeData {

	public static EnumOptionData ItemClassType(final int id) {
		return ItemClassType(ItemEnumType.fromInt(id));
	}

	public static EnumOptionData ItemClassType(final ItemEnumType type) {
		final String codePrefix = "deposit.interest.compounding.period.";
		EnumOptionData optionData = null;
		switch (type) {
		case HARDWARE:
			optionData = new EnumOptionData(ItemEnumType.HARDWARE.getValue().longValue(), codePrefix + ItemEnumType.HARDWARE.getCode(), "HARDWARE");
			break;
		case SOFT_CHARGE:
			optionData = new EnumOptionData(ItemEnumType.SOFT_CHARGE.getValue().longValue(), codePrefix + ItemEnumType.SOFT_CHARGE.getCode(), "SOFT CHARGE");
			break;

		case PREPAID_CARD:
			optionData = new EnumOptionData(ItemEnumType.PREPAID_CARD.getValue().longValue(), codePrefix + ItemEnumType.PREPAID_CARD.getCode(), "PREPAID CARD");
			break;
			
		case EVENT:
			optionData = new EnumOptionData(ItemEnumType.EVENT.getValue().longValue(), codePrefix + ItemEnumType.EVENT.getCode(), "EVENT");
			break;
			
	

		default:
			optionData = new EnumOptionData(ItemEnumType.INVALID.getValue().longValue(), ItemEnumType.INVALID.getCode(), "INVALID");
			break;
		}
		return optionData;
	}

}
