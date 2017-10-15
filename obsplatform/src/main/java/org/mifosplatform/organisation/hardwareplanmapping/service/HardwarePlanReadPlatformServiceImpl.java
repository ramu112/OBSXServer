package org.mifosplatform.organisation.hardwareplanmapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwareMappingDetailsData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class HardwarePlanReadPlatformServiceImpl implements HardwarePlanReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public HardwarePlanReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<HardwarePlanData> retrievePlanData(String itemCode) {

		this.context.authenticatedUser();
		final PlanDataMapper mapper = new PlanDataMapper();
		String sql = "select " + mapper.schema();
		if(itemCode != null){
			sql = sql + " where h.item_code = '"+itemCode+"' ";
		}
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class PlanDataMapper implements RowMapper<HardwarePlanData> {

		public String schema() {
			return "h.id as id,h.plan_code as planCode,h.item_code as itemCode,h.provisioning_id as provisioningId,cv.code_value as provisioningValue" +
					" from b_hw_plan_mapping h JOIN m_code_value cv ON cv.id = h.provisioning_id JOIN m_code c ON c.id = cv.code_id";

		}

		@Override
		public HardwarePlanData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String planCode = rs.getString("planCode");
			final String itemCode = rs.getString("itemCode");
			final Long provisioningId = rs.getLong("provisioningId");
			final String provisioningValue = rs.getString("provisioningValue");
			
			return new HardwarePlanData(id, planCode, itemCode,provisioningId,provisioningValue);
		}
	}

	@Override
	public List<ItemData> retrieveItems() {
		
		this.context.authenticatedUser();
		final ItemDataMaper mapper = new ItemDataMaper();
		final String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class ItemDataMaper implements RowMapper<ItemData> {

		public String schema() {
			return " i.id as id,i.item_code as itemCode,i.item_description as itemDescription from b_item_master i";

		}

		@Override
		public ItemData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final String itemCode = rs.getString("itemCode");
			final String itemDescription = rs.getString("itemDescription");
			
			return new ItemData(id, itemCode, itemDescription, null, null,
					null, 0, null, null, null, null, null,null,null,null);

		}
	}

	@Override
	public List<PlanCodeData> retrievePlans() {

		this.context.authenticatedUser();
		final PlanDataMaper mapper = new PlanDataMaper();
		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class PlanDataMaper implements RowMapper<PlanCodeData> {

		public String schema() {
			return " p.id as id,p.plan_code as planCode,p.plan_description as planDescription from b_plan_master p where p.is_deleted = 'N'";

		}

		@Override
		public PlanCodeData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");

			return  new PlanCodeData(id, planCode, null,null,planDescription);
		}

	}

	@Override
	public HardwarePlanData retrieveSinglePlanData(final Long planId) {
		
		this.context.authenticatedUser();
		final String sql = "select b.id as id,b.item_code as itemcode,b.plan_code as plancode,b.provisioning_id as provisioningId,cv.code_value as provisioningValue" +
				" from b_hw_plan_mapping b JOIN m_code_value cv on cv.id = b.provisioning_id JOIN m_code c on c.id = cv.code_id where b.id=?";
		final RowMapper<HardwarePlanData> rm = new ServiceMapper();

		return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { planId });
	}

	private static final class ServiceMapper implements RowMapper<HardwarePlanData> {

		@Override
		public HardwarePlanData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String planCode = rs.getString("plancode");
			final String itemCode = rs.getString("itemcode");
			final Long provisioningId = rs.getLong("provisioningId");
			final String provisioningValue = rs.getString("provisioningValue"); 

			return new HardwarePlanData(id, planCode, itemCode,provisioningId,provisioningValue);

		}
	}

	@Override
	public List<HardwarePlanData> retrieveItems(final String itemCode) {

		context.authenticatedUser();
		final PlanDataMapper mapper = new PlanDataMapper();
		final String sql = "select " + mapper.schema() + " where h.item_code=?";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { itemCode });
	}

	@Override
	public List<HardwareMappingDetailsData> getPlanDetailsByItemCode(final String itemCode, final Long clientId) {
		
		try {
			this.context.authenticatedUser();
			final HardwareMapper mapper = new HardwareMapper();
			final String sql = "select" + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] {itemCode, clientId });

		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}

	}

	public List<HardwarePlanData> retrieveHardwareMappingsOfAPlan(String planCode) {

		this.context.authenticatedUser();
		final PlanDataMapper mapper = new PlanDataMapper();
		String sql = "select " + mapper.schema()+" where h.plan_code = '"+planCode+"' ";
		
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}
	
	private static final class HardwareMapper implements RowMapper<HardwareMappingDetailsData> {

		public String schema() {

			return " p.id AS planId, p.plan_code AS planCode, o.id AS orderId FROM b_orders o,b_plan_master p,b_hw_plan_mapping phw"
					+ "  WHERE p.id = o.plan_id  AND phw.plan_code = p.plan_code and phw.item_code =? and o.client_id=?";
		}

		@Override
		public HardwareMappingDetailsData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long planId = rs.getLong("planId");
			Long orderId = rs.getLong("orderId");
			String planCode = rs.getString("planCode");

			return new HardwareMappingDetailsData(planId, orderId, planCode);
		}

	}

}
