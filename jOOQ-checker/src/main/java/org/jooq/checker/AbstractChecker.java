/*
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
package org.jooq.checker;

import static org.checkerframework.javacutil.TreeUtils.elementFromDeclaration;
import static org.checkerframework.javacutil.TreeUtils.enclosingClass;
import static org.checkerframework.javacutil.TreeUtils.enclosingMethod;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.lang.model.element.Element;

import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.source.SourceChecker;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePath;

/**
 * Common base class for checkers.
 *
 * @author Lukas Eder
 */
abstract class AbstractChecker extends SourceChecker {

    void error(Object node, String message) {
        getChecker().report(Result.failure(message, node), node);
    }

    void warn(Object node, String message) {
        getChecker().report(Result.warning(message, node), node);
    }

    static void print(Printer printer) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("error.txt"))){
            writer.println("This is probably a bug in jOOQ-checker.");
            writer.println("If you think this is a bug in jOOQ, please report it here: https://github.com/jOOQ/jOOQ/issues/new");
            writer.println("---------------------------------------------------------------------");

            printer.print(writer);
        }
        catch (IOException ignore) {}
    }

    static Element enclosing(TreePath path) {
        MethodTree enclosingMethod = enclosingMethod(path);

        if (enclosingMethod != null)
            return elementFromDeclaration(enclosingMethod);

        ClassTree enclosingClass = enclosingClass(path);
        return elementFromDeclaration(enclosingClass);
    }

    interface Printer {
        void print(PrintWriter writer);
    }
}
