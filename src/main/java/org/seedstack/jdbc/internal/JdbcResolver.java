/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import org.seedstack.jdbc.Jdbc;
import org.seedstack.shed.reflect.StandardAnnotationResolver;

import java.lang.reflect.Method;

class JdbcResolver extends StandardAnnotationResolver<Method, Jdbc> {
    static JdbcResolver INSTANCE = new JdbcResolver();

    private JdbcResolver() {
        // no external instantiation allowed
    }
}
