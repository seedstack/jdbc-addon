/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 17 févr. 2015
 */
package org.seedstack.seed.persistence.jdbc.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation accompanies the {@link org.seedstack.seed.transaction.api.Transactional} annotation to define the transaction as a JDBC one and
 * specify the concerned JDBC datasource.
 * 
 * @author yves.dautremay@mpsa.com
 */
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Jdbc {

    /**
     * Specifies the data source name to use
     * 
     * @return the data source name from which the connection will be used
     */
    String value() default "";
}
