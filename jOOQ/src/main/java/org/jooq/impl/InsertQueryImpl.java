/**
 * Copyright (c) 2009-2016, Data Geekery GmbH (http://www.datageekery.com)
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package org.jooq.impl;

import static java.util.Arrays.asList;
import static org.jooq.Clause.INSERT;
import static org.jooq.Clause.INSERT_INSERT_INTO;
import static org.jooq.Clause.INSERT_ON_DUPLICATE_KEY_UPDATE;
import static org.jooq.Clause.INSERT_ON_DUPLICATE_KEY_UPDATE_ASSIGNMENT;
import static org.jooq.Clause.INSERT_RETURNING;
import static org.jooq.Clause.INSERT_SELECT;
import static org.jooq.SQLDialect.MARIADB;
import static org.jooq.SQLDialect.MYSQL;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;

import java.util.Arrays;
import java.util.Map;

import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.Merge;
import org.jooq.MergeNotMatchedStep;
import org.jooq.MergeOnConditionStep;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.exception.SQLDialectNotSupportedException;

/**
 * @author Lukas Eder
 */
class InsertQueryImpl<R extends Record> extends AbstractStoreQuery<R> implements InsertQuery<R> {

    private static final long        serialVersionUID = 4466005417945353842L;
    private static final Clause[]    CLAUSES          = { INSERT };

    private final FieldMapForUpdate  updateMap;
    private final FieldMapsForInsert insertMaps;
    private Select<?>                select;
    private boolean                  defaultValues;
    private boolean                  onDuplicateKeyUpdate;
    private boolean                  onDuplicateKeyIgnore;

    InsertQueryImpl(Configuration configuration, WithImpl with, Table<R> into) {
        super(configuration, with, into);

        this.updateMap = new FieldMapForUpdate(INSERT_ON_DUPLICATE_KEY_UPDATE_ASSIGNMENT);
        this.insertMaps = new FieldMapsForInsert();
    }

    @Override
    public final void newRecord() {
        insertMaps.newRecord();
    }

    @Override
    protected final FieldMapForInsert getValues() {
        return insertMaps.getMap();
    }

    @Override
    public final void addRecord(R record) {
        newRecord();
        setRecord(record);
    }

    @Override
    public final void onDuplicateKeyUpdate(boolean flag) {
        this.onDuplicateKeyIgnore = false;
        this.onDuplicateKeyUpdate = flag;
    }

    @Override
    public final void onDuplicateKeyIgnore(boolean flag) {
        this.onDuplicateKeyUpdate = false;
        this.onDuplicateKeyIgnore = flag;
    }

    @Override
    public final <T> void addValueForUpdate(Field<T> field, T value) {
        updateMap.put(field, Tools.field(value, field));
    }

    @Override
    public final <T> void addValueForUpdate(Field<T> field, Field<T> value) {
        updateMap.put(field, Tools.field(value, field));
    }

    @Override
    public final void addValuesForUpdate(Map<? extends Field<?>, ?> map) {
        updateMap.set(map);
    }

    @Override
    public final void setDefaultValues() {
        defaultValues = true;
    }

    @Override
    public final void setSelect(Field<?>[] f, Select<?> s) {
        insertMaps.getMap().putFields(Arrays.asList(f));
        select = s;
    }

    @Override
    public final void addValues(Map<? extends Field<?>, ?> map) {
        insertMaps.getMap().set(map);
    }

    @Override
    final void accept0(Context<?> ctx) {

        // ON DUPLICATE KEY UPDATE clause
        // ------------------------------
        if (onDuplicateKeyUpdate) {
            switch (ctx.family()) {

                // MySQL has a nice syntax for this
                case CUBRID:
                case MARIADB:
                case MYSQL: {
                    toSQLInsert(ctx);
                    ctx.formatSeparator()
                       .start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                       .keyword("on duplicate key update")
                       .sql(' ')
                       .visit(updateMap)
                       .end(INSERT_ON_DUPLICATE_KEY_UPDATE);

                    break;
                }

                case POSTGRES: {
                    toSQLInsert(ctx);
                    ctx.formatSeparator()
                       .start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                       .keyword("on conflict")
                       .sql(" (");

                    if (table.getPrimaryKey() == null) {
                        ctx.sql("[unknown primary key]");
                    }
                    else {
                        boolean qualify = ctx.qualify();

                        ctx.qualify(false)
                           .visit(new Fields<Record>(table.getPrimaryKey().getFields()))
                           .qualify(qualify);
                    }

                    ctx.sql(") ")
                       .keyword("do update")
                       .formatSeparator()
                       .keyword("set")
                       .sql(' ')
                       .formatIndentLockStart()
                       .visit(updateMap)
                       .formatIndentLockEnd()
                       .end(INSERT_ON_DUPLICATE_KEY_UPDATE);

                    break;
                }

                // Some dialects can't really handle this clause. Emulation should be done in two steps
                case H2: {
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY UPDATE clause cannot be emulated for " + ctx.dialect());
                }

                // Some databases allow for emulating this clause using a MERGE statement







                case HSQLDB: {
                    ctx.visit(toMerge(ctx.configuration()));
                    break;
                }

                default:
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY UPDATE clause cannot be emulated for " + ctx.dialect());
            }
        }

        // ON DUPLICATE KEY IGNORE clause
        // ------------------------------
        else if (onDuplicateKeyIgnore) {
            switch (ctx.dialect()) {

                // MySQL has a nice, native syntax for this
                case MARIADB:
                case MYSQL:
                case SQLITE: {
                    toSQLInsert(ctx);
                    ctx.start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                       .end(INSERT_ON_DUPLICATE_KEY_UPDATE);
                    break;
                }

                case POSTGRES_9_5: {
                    toSQLInsert(ctx);
                    ctx.formatSeparator()
                       .start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                       .keyword("on conflict do nothing")
                       .end(INSERT_ON_DUPLICATE_KEY_UPDATE);
                    break;
                }

                // CUBRID can emulate this using ON DUPLICATE KEY UPDATE
                case CUBRID: {
                    FieldMapForUpdate update = new FieldMapForUpdate(INSERT_ON_DUPLICATE_KEY_UPDATE_ASSIGNMENT);
                    Field<?> field = table.field(0);
                    update.put(field, field);

                    toSQLInsert(ctx);
                    ctx.formatSeparator()
                       .start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                       .keyword("on duplicate key update")
                       .sql(' ')
                       .visit(update)
                       .end(INSERT_ON_DUPLICATE_KEY_UPDATE);

                    break;
                }

                // Some databases allow for emulating this clause using a MERGE statement



















                case HSQLDB: {
                    ctx.visit(toMerge(ctx.configuration()));
                    break;
                }

                default: {
                    ctx.visit(toInsertSelect(ctx.configuration()));
                    break;
                }
            }
        }

        // Default mode
        // ------------
        else {
            toSQLInsert(ctx);
            ctx.start(INSERT_ON_DUPLICATE_KEY_UPDATE)
               .end(INSERT_ON_DUPLICATE_KEY_UPDATE);
        }

        ctx.start(INSERT_RETURNING);
        toSQLReturning(ctx);
        ctx.end(INSERT_RETURNING);
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    private final void toSQLInsert(Context<?> ctx) {
        boolean declareTables = ctx.declareTables();

        ctx.start(INSERT_INSERT_INTO)
           .keyword("insert")
           .sql(' ')
           // [#1295] [#4376] MySQL and SQLite have native syntaxes for
           //                 INSERT [ OR ] IGNORE
           .keyword((onDuplicateKeyIgnore && asList(MARIADB, MYSQL).contains(ctx.family()))
                  ? "ignore "
                  : (onDuplicateKeyIgnore && SQLDialect.SQLITE == ctx.family())
                  ? "or ignore "
                  : ""
           )
           .keyword("into")
           .sql(' ')
           .declareTables(true)
           .visit(table)
           .declareTables(declareTables);

        // [#1506] with DEFAULT VALUES, we might not have any columns to render
        if (insertMaps.isExecutable()) {
            ctx.sql(' ');
            insertMaps.insertMaps.get(0).toSQLReferenceKeys(ctx);
        }

        ctx.end(INSERT_INSERT_INTO);

        if (select != null) {
            ctx.formatSeparator()
               .start(INSERT_SELECT)
               .visit(select)
               .end(INSERT_SELECT);
        }
        else if (defaultValues) {
            switch (ctx.family()) {






                case DERBY:
                case MARIADB:
                case MYSQL:
                    ctx.formatSeparator()
                       .keyword("values")
                       .sql('(');

                    int count = table.fields().length;
                    String separator = "";

                    for (int i = 0; i < count; i++) {
                        ctx.sql(separator);
                        ctx.keyword("default");
                        separator = ", ";
                    }

                    ctx.sql(')');
                    break;

                default:
                    ctx.formatSeparator()
                       .keyword("default values");
                    break;
            }
        }
        else {
            ctx.visit(insertMaps);
        }
    }

    private final QueryPart toInsertSelect(Configuration configuration) {
        if (table.getPrimaryKey() != null) {
            return create(configuration)
                .insertInto(table)
                .columns(insertMaps.getMap().keySet())
                .select(
                    select(insertMaps.getMap().values())
                    .whereNotExists(
                        selectOne()
                        .from(table)
                        .where(matchByPrimaryKey())
                    )
                );
        }
        else {
            throw new IllegalStateException("The ON DUPLICATE KEY IGNORE/UPDATE clause cannot be emulated when inserting into non-updatable tables : " + table);
        }
    }

    private final Merge<R> toMerge(Configuration configuration) {
        if (table.getPrimaryKey() != null) {
            MergeOnConditionStep<R> on =
            create(configuration).mergeInto(table)
                                 .usingDual()
                                 .on(matchByPrimaryKey());

            // [#1295] Use UPDATE clause only when with ON DUPLICATE KEY UPDATE,
            // not with ON DUPLICATE KEY IGNORE
            MergeNotMatchedStep<R> notMatched = on;
            if (onDuplicateKeyUpdate) {
                notMatched = on.whenMatchedThenUpdate()
                               .set(updateMap);
            }

            return notMatched.whenNotMatchedThenInsert(insertMaps.getMap().keySet())
                             .values(insertMaps.getMap().values());
        }
        else {
            throw new IllegalStateException("The ON DUPLICATE KEY IGNORE/UPDATE clause cannot be emulated when inserting into non-updatable tables : " + table);
        }
    }

    /**
     * Produce a {@link Condition} that matches existing rows by the inserted or
     * updated primary key values.
     */
    @SuppressWarnings("unchecked")
    private final Condition matchByPrimaryKey() {
        Condition condition = null;

        for (Field<?> f : table.getPrimaryKey().getFields()) {
            Field<Object> field = (Field<Object>) f;
            Field<Object> value = (Field<Object>) insertMaps.getMap().get(field);

            Condition other = field.equal(value);

            if (condition == null) {
                condition = other;
            }
            else {
                condition = condition.and(other);
            }
        }

        return condition;
    }

    @Override
    public final boolean isExecutable() {
        return insertMaps.isExecutable() || defaultValues || select != null;
    }
}
