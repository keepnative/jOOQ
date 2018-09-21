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

import static org.jooq.Clause.CREATE_VIEW;
import static org.jooq.Clause.CREATE_VIEW_AS;
import static org.jooq.Clause.CREATE_VIEW_NAME;
import static org.jooq.SQLDialect.SQLITE;
import static org.jooq.conf.ParamType.INLINED;
import static org.jooq.impl.DSL.selectFrom;
import static org.jooq.impl.DSL.table;

import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateViewAsStep;
import org.jooq.CreateViewFinalStep;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.conf.ParamType;

/**
 * @author Lukas Eder
 */
class CreateViewImpl<R extends Record> extends AbstractQuery implements

    // Cascading interface implementations for CREATE VIEW behaviour
    CreateViewAsStep<R>,
    CreateViewFinalStep {


    /**
     * Generated UID
     */
    private static final long     serialVersionUID = 8904572826501186329L;
    private static final Clause[] CLAUSES          = { CREATE_VIEW };

    private final Table<?>        view;
    private final Field<?>[]      fields;
    private Select<?>             select;

    CreateViewImpl(Configuration configuration, Table<?> view, Field<?>[] fields) {
        super(configuration);

        this.view = view;
        this.fields = fields;
    }

    // ------------------------------------------------------------------------
    // XXX: DSL API
    // ------------------------------------------------------------------------

    @Override
    public final CreateViewFinalStep as(Select<? extends R> s) {
        this.select = s;
        return this;
    }

    // ------------------------------------------------------------------------
    // XXX: QueryPart API
    // ------------------------------------------------------------------------

    @Override
    public final void accept(Context<?> ctx) {

        // [#3835] SQLite doesn't like renaming columns at the view level
        boolean rename = fields != null && fields.length > 0;
        boolean renameSupported = ctx.family() != SQLITE;

        // [#4806] CREATE VIEW doesn't accept parameters in most databases
        ParamType paramType = ctx.paramType();

        ctx.start(CREATE_VIEW_NAME)
           .keyword("create view")
           .sql(' ')
           .visit(view);

        if (rename && renameSupported) {
            boolean qualify = ctx.qualify();

            ctx.sql('(')
               .qualify(false)
               .visit(new QueryPartList<Field<?>>(fields))
               .qualify(qualify)
               .sql(')');
        }

        ctx.end(CREATE_VIEW_NAME)
           .formatSeparator()
           .keyword("as")
           .formatSeparator()
           .start(CREATE_VIEW_AS)
           .paramType(INLINED)
           .visit(
               rename && !renameSupported
             ? selectFrom(table(select).as("t", Tools.fieldNames(fields)))
             : select)
           .paramType(paramType)
           .end(CREATE_VIEW_AS);
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }
}
