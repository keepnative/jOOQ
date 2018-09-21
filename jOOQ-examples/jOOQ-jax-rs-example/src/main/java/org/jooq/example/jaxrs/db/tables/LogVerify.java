/**
 * This class is generated by jOOQ
 */
package org.jooq.example.jaxrs.db.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LogVerify extends org.jooq.impl.TableImpl<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord> {

	private static final long serialVersionUID = 475379908;

	/**
	 * The singleton instance of <code>license_server.log_verify</code>
	 */
	public static final org.jooq.example.jaxrs.db.tables.LogVerify LOG_VERIFY = new org.jooq.example.jaxrs.db.tables.LogVerify();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord> getRecordType() {
		return org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord.class;
	}

	/**
	 * The column <code>license_server.log_verify.id</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord, java.lang.Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT, this);

	/**
	 * The column <code>license_server.log_verify.licensee</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord, java.lang.String> LICENSEE = createField("licensee", org.jooq.impl.SQLDataType.CLOB, this);

	/**
	 * The column <code>license_server.log_verify.license</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord, java.lang.String> LICENSE = createField("license", org.jooq.impl.SQLDataType.CLOB, this);

	/**
	 * The column <code>license_server.log_verify.request_ip</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord, java.lang.String> REQUEST_IP = createField("request_ip", org.jooq.impl.SQLDataType.VARCHAR.length(50), this);

	/**
	 * The column <code>license_server.log_verify.version</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord, java.lang.String> VERSION = createField("version", org.jooq.impl.SQLDataType.VARCHAR.length(50), this);

	/**
	 * The column <code>license_server.log_verify.match</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord, java.lang.Boolean> MATCH = createField("match", org.jooq.impl.SQLDataType.BOOLEAN, this);

	/**
	 * Create a <code>license_server.log_verify</code> table reference
	 */
	public LogVerify() {
		super("log_verify", org.jooq.example.jaxrs.db.LicenseServer.LICENSE_SERVER);
	}

	/**
	 * Create an aliased <code>license_server.log_verify</code> table reference
	 */
	public LogVerify(java.lang.String alias) {
		super(alias, org.jooq.example.jaxrs.db.LicenseServer.LICENSE_SERVER, org.jooq.example.jaxrs.db.tables.LogVerify.LOG_VERIFY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord, java.lang.Long> getIdentity() {
		return org.jooq.example.jaxrs.db.Keys.IDENTITY_LOG_VERIFY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord> getPrimaryKey() {
		return org.jooq.example.jaxrs.db.Keys.PK_LOG_VERIFY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.jooq.example.jaxrs.db.tables.records.LogVerifyRecord>>asList(org.jooq.example.jaxrs.db.Keys.PK_LOG_VERIFY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.example.jaxrs.db.tables.LogVerify as(java.lang.String alias) {
		return new org.jooq.example.jaxrs.db.tables.LogVerify(alias);
	}
}
