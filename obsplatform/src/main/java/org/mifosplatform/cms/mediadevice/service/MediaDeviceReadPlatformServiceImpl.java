package org.mifosplatform.cms.mediadevice.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.cms.mediadevice.data.MediaDeviceData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class MediaDeviceReadPlatformServiceImpl implements MediaDeviceReadPlatformService{
	
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public MediaDeviceReadPlatformServiceImpl (final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	

	@Override
	public MediaDeviceData retrieveDeviceDetails(String deviceId) {
		try {
			
			this.context.authenticatedUser();
			final MediaDeviceMapper eventMasterMapper = new MediaDeviceMapper();
			final String sql = eventMasterMapper.mediaDeviceSchema();
			return jdbcTemplate.queryForObject(sql, eventMasterMapper,new Object[] {deviceId});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class MediaDeviceMapper implements RowMapper<MediaDeviceData> {
		public String mediaDeviceSchema() {
			/* return "  SELECT a.id AS deviceId,a.client_id AS clientId,mc.code_value AS clientType,mc.id AS clientTypeId,b.balance_amount as balanceAmount " +
					"  FROM b_allocation a,m_code_value mc, m_client c left join b_client_balance b on b.client_id = c.id WHERE  a.client_id = c.id AND " +
					"  mc.id = c.category_type AND serial_no = ? AND a.is_deleted = 'N' UNION SELECT a.id,a.client_id,mc.code_value,mc.id, b.balance_amount as balanceAmount" +
					"  FROM b_owned_hardware a, m_code_value mc, m_client c left join b_client_balance b on b.client_id = c.id WHERE a.client_id = c.id AND mc.id = c.category_type" +
					"  AND serial_number =?  and a.is_deleted ='N'"; */
			
			return "SELECT ifnull(a.id, oh.id) AS deviceId," +
					"ifnull(a.client_id,oh.client_id) AS clientId,cv.code_value AS clientType,cv.id AS clientTypeId," +
					"cb.balance_amount as balanceAmount ,cc.currency " +
					"from m_client c " +
					"left join m_code_value cv on (c.category_type=cv.id ) " +
					"left join b_allocation a on (c.id =a.client_id AND a.is_deleted = 'N' ) " +
					"left join b_client_balance cb on (c.id =cb.client_id ) " +
					"left join b_owned_hardware oh on (c.id =oh.client_id AND oh.is_deleted = 'N') " +
					"left join b_client_address ca on (c.id=ca.client_id and address_key='PRIMARY') " +
					"left join b_country_currency cc on (cc.country=ca.country ) " +
					"WHERE ifnull(a.serial_no, oh.serial_number) = ? group by deviceId";
					
		}
		
		public String MediaDeviceBasedOnCientIdSchema() {
			
			return "SELECT null as deviceId,c.id clientId,cv.code_value AS clientType,cv.id AS clientTypeId," +
					"cb.balance_amount as balanceAmount ,cc.currency from m_client c left join m_code_value cv on (c.category_type=cv.id )" +
					" left join b_client_balance cb on (c.id =cb.client_id ) left join b_client_address ca on (c.id=ca.client_id and address_key='PRIMARY')" +
					" left join b_country_currency cc on (cc.country=ca.country ) WHERE c.id = ? group by c.id";
  
		}
		
		@Override
		public MediaDeviceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long deviceId = rs.getLong("deviceId");
			final Long clientId = rs.getLong("clientId");
			final String clientType = rs.getString("clientType");
			final Long clientTypeId = rs.getLong("clientTypeId");
			final BigDecimal balanceAmount=rs.getBigDecimal("balanceAmount");
			final String currency = rs.getString("currency");
			return new MediaDeviceData(deviceId,clientId,clientType,clientTypeId,balanceAmount,currency);
		}
	}

	@Override
	public List<MediaDeviceData> retrieveDeviceDataDetails(String deviceId) {
		try {
			final MediaDeviceMapper mapper = new MediaDeviceMapper();
			final String sql = mapper.mediaDeviceSchema();
			return jdbcTemplate.query(sql, mapper,new Object[] {deviceId,deviceId});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
/*	private static final class EventMasterMapper1 implements RowMapper<MediaDeviceData> {
		public String eventMasterSchema() {
			return "select a.id AS deviceId,a.client_id AS clientId,mc.code_value AS clientType,mc.id as clientTypeId FROM b_allocation a, m_client c,m_code_value mc WHERE a.client_id = c.id AND	 mc.id=c.category_type and serial_no=? union select a.id,a.client_id ,mc.code_value ,mc.id FROM b_owned_hardware a, m_client c,m_code_value mc WHERE a.client_id = c.id AND	 mc.id=c.category_type and serial_number=?"; 
  
		}
		@Override
		public MediaDeviceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long deviceId = rs.getLong("deviceId");
			final Long clientId = rs.getLong("clientId");
			final String clientType = rs.getString("clientType");
			final Long clientTypeId = rs.getLong("clientTypeId");
			return new MediaDeviceData(deviceId,clientId,clientType,clientTypeId);
		}*/
	

	
	@Override
	public List<PlanData> retrievePlanDetails(Long clientId) {
		PlanMasterMapper mapper = new PlanMasterMapper();
		String sql = mapper.planMasterSchema();
		return jdbcTemplate.query(sql,mapper, new Object[]{clientId});
	}
	
	private static final class PlanMasterMapper implements RowMapper<PlanData> {
		public String planMasterSchema() {
			return "select pm.id as id, pm.plan_code as planCode, pm.plan_description as planDescription from b_plan_master pm inner join b_orders o on o.plan_id = pm.id where o.client_id=? and pm.is_prepaid = 'Y' and pm.plan_status=1";/*"select pm.id as id, pm.plan_code as planCode, pm.plan_description as planDescription from b_plan_master pm where plan_status=1 and is_prepaid='Y'";*/ 
  
		}
		@Override
		public PlanData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			return new PlanData(id, planCode, planDescription, null);
		}
	}
	
	
	
	
	@Override
	public List<PlanData> retrievePlanPostpaidDetails(Long clientId) {
		PlanMasterMapperPostPaid mapper = new PlanMasterMapperPostPaid();
		String sql = mapper.planMasterSchema();
		return jdbcTemplate.query(sql,mapper, new Object[]{clientId});	
	}
	
	private static final class PlanMasterMapperPostPaid implements RowMapper<PlanData> {
		public String planMasterSchema() {
			return "select pm.id as id, pm.plan_code as planCode, pm.plan_description as planDescription from b_plan_master pm inner join b_orders o on o.plan_id = pm.id where o.client_id=? and pm.is_prepaid = 'N' and pm.plan_status=1";/*"select pm.id as id, pm.plan_code as planCode, pm.plan_description as planDescription from b_plan_master pm where plan_status=1 and is_prepaid='Y'";*/ 
  
		}
		@Override
		public PlanData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			return new PlanData(id, planCode, planDescription, null);
		}
	}
	
	private static final class MediaDeviceDetailsMapper implements RowMapper<Long> {
		
		@Override
		public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long deviceId = rs.getLong("deviceIds");
			return deviceId;
		}
		
	}
	

	@Override
	public Long retrieveDeviceDataDetails(Long clientId,String deviceId) {
		
		try {
			final MediaDeviceDetailsMapper mapper = new MediaDeviceDetailsMapper();
			final String sql ="select count(*) as deviceIds from b_owned_hardware  where client_id = ? and status = 'ACTIVE' " +
					" and provisioning_serial_number != ?  and is_deleted='N'";
			return jdbcTemplate.queryForObject(sql, mapper,new Object[] {clientId,deviceId});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public MediaDeviceData retrieveClientDetails(String clientId) {
		try {
			
			this.context.authenticatedUser();
			final MediaDeviceMapper eventMasterMapper = new MediaDeviceMapper();
			final String sql = eventMasterMapper.MediaDeviceBasedOnCientIdSchema();
			return jdbcTemplate.queryForObject(sql, eventMasterMapper,new Object[] {clientId});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
}

