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
public class License extends org.jooq.impl.TableImpl<org.jooq.example.jaxrs.db.tables.records.LicenseRecord> {

	private static final long serialVersionUID = -1374757615;

	/**
	 * The singleton instance of <code>license_server.license</code>
	 */
	public static final org.jooq.example.jaxrs.db.tables.License LICENSE = new org.jooq.example.jaxrs.db.tables.License();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.jooq.example.jaxrs.db.tables.records.LicenseRecord> getRecordType() {
		return org.jooq.example.jaxrs.db.tables.records.LicenseRecord.class;
	}

	/**
	 * The column <code>license_server.license.id</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LicenseRecord, java.lang.Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT, this);

	/**
	 * The column <code>license_server.license.license_date</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LicenseRecord, java.sql.Timestamp> LICENSE_DATE = createField("license_date", org.jooq.impl.SQLDataType.TIMESTAMP, this);

	/**
	 * The column <code>license_server.license.licensee</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LicenseRecord, java.lang.String> LICENSEE = createField("licensee", org.jooq.impl.SQLDataType.CLOB, this);

	/**
	 * The column <code>license_server.license.license</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LicenseRecord, java.lang.String> LICENSE_ = createField("license", org.jooq.impl.SQLDataType.CLOB, this);

	/**
	 * The column <code>license_server.license.version</code>.
	 */
	public final org.jooq.TableField<org.jooq.example.jaxrs.db.tables.records.LicenseRecord, java.lang.String> VERSION = createField("version", org.jooq.impl.SQLDataType.VARCHAR.length(50), this);

	/**
	 * Create a <code>license_server.license</code> table reference
	 */
	public License() {
		super("license", org.jooq.example.jaxrs.db.LicenseServer.LICENSE_SERVER);
	}

	/**
	 * Create an aliased <code>license_server.license</code> table reference
	 */
	public License(java.lang.String alias) {
		super(alias, org.jooq.example.jaxrs.db.LicenseServer.LICENSE_SERVER, org.jooq.example.jaxrs.db.tables.License.LICENSE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.jooq.example.jaxrs.db.tables.records.LicenseRecord, java.lang.Long> getIdentity() {
		return org.jooq.example.jaxrs.db.Keys.IDENTITY_LICENSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.jooq.example.jaxrs.db.tables.records.LicenseRecord> getPrimaryKey() {
		return org.jooq.example.jaxrs.db.Keys.PK_LICENSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.jooq.example.jaxrs.db.tables.records.LicenseRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.jooq.example.jaxrs.db.tables.records.LicenseRecord>>asList(org.jooq.example.jaxrs.db.Keys.PK_LICENSE, org.jooq.example.jaxrs.db.Keys.UK_LICENSE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.example.jaxrs.db.tables.License as(java.lang.String alias) {
		return new org.jooq.example.jaxrs.db.tables.License(alias);
	}
}
