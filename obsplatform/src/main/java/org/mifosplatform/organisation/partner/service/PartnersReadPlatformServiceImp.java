package org.mifosplatform.organisation.partner.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.partner.data.PartnersData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PartnersReadPlatformServiceImp implements PartnersReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public PartnersReadPlatformServiceImp(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Collection<PartnersData> retrieveAllPartners() {

	try {
		context.authenticatedUser();
		final PartnerMapper mapper = new PartnerMapper();
		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	} catch (final EmptyResultDataAccessException accessException) {
		return null;
	}

}

private static final class PartnerMapper implements RowMapper<PartnersData> {

		public String schema() {
			return " a.id as infoId,a.partner_currency as currency,a.credit_limit as creditLimit,a.is_collective as isCollective,o.name as partnerName,"
					+ "o.id as officeId,o.parent_id as parentId,o.external_id AS externalId,o.opening_date AS openingDate,parent.id AS parentId,"
					+ "parent.name AS parentName,c.code_value as officeType,  a.contact_name as contactName, ad.city as city, ad.state as state,"
					+ "ad.country as country,ad.email_id as email,ad.phone_number as phoneNumber,ad.office_number as officeNumber,au.id as userId,au.username as loginName,"
					+ "IFNULL(ob.balance_amount,0) as balanceAmount,ad.company_logo as companyLogo from m_office o left join m_office AS parent on parent.id = o.parent_id " 
					+ "inner join m_office_additional_info a ON o.id=a.office_id  inner join b_office_address ad on o.id = ad.office_id "
					+ "inner join m_appuser au on o.id=au.office_id left join m_office_balance ob ON ob.office_id=o.id "
					+ "left join m_code_value c on c.id = o.office_type ";
		}

	@Override
	public PartnersData mapRow(final ResultSet rs,final int rowNum) throws SQLException {

		
	final Long id = rs.getLong("infoId");
	final Long officeId = rs.getLong("officeId");
	final String partnerName = rs.getString("partnerName");
	final BigDecimal creditLimit = rs.getBigDecimal("creditLimit");
	final String currency = rs.getString("currency");
	final Long parentId = rs.getLong("parentId");
	final String parentName = rs.getString("parentName");
	final String officeType = rs.getString("officeType");
	final LocalDate openingDate = JdbcSupport.getLocalDate(rs, "openingDate");
	final String loginName =rs.getString("loginName");
	final String city =rs.getString("city");
	final String state =rs.getString("state");
	final String country =rs.getString("country");
	final String email =rs.getString("email");
	final String phoneNumber =rs.getString("phoneNumber");
	final String officeNumber =rs.getString("officeNumber");
	final String isCollective = rs.getString("isCollective");
	final BigDecimal balanceAmount =rs.getBigDecimal("balanceAmount");
	final Long userId = rs.getLong("userId");
	final String contactName =rs.getString("contactName");
	final String companyLogo = rs.getString("companyLogo");
	
	return new PartnersData(officeId,id,partnerName,creditLimit,currency,parentId,parentName,officeType,
			     openingDate,loginName,city,state,country,email,phoneNumber,isCollective,balanceAmount,officeNumber,contactName,userId,companyLogo);
	

	}
}

@Override
public PartnersData retrieveSinglePartnerDetails(final Long partnerId) {
	
	try{
		context.authenticatedUser();
		final PartnerMapper mapper=new PartnerMapper();
		final String sql="select " + mapper.schema() + " where a.id= ?";
		return this.jdbcTemplate.queryForObject(sql, mapper,new Object[]{partnerId});
	}catch (final EmptyResultDataAccessException accessException) {
		return null;
	}
}

	@Override
	public PartnersData retrievePartnerImage(Long userId) {

		try {
			context.authenticatedUser();
			final PartnerImage mapper = new PartnerImage();
			final String sql = "select ad.company_logo as imageKey from b_office_address ad inner join m_appuser au on ad.office_id=au.office_id where au.id= ?";
			return this.jdbcTemplate.queryForObject(sql, mapper,new Object[] { userId });
		} catch (final EmptyResultDataAccessException accessException) {
			return null;
		}
	}

	private static class PartnerImage implements RowMapper<PartnersData> {

		@Override
		public PartnersData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final String imageKey =rs.getString("imageKey");
			
			return new PartnersData(null,null,imageKey);		
			}

	}
}
