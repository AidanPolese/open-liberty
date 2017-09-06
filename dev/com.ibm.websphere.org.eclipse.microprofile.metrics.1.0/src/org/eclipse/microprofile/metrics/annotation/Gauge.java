/**********************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *               2010-2013 Coda Hale, Yammer.com
 *
 * See the NOTICES file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 **********************************************************************/
package org.eclipse.microprofile.metrics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * An annotation for marking a method of an annotated object as a gauge.
 * Given a method like this:
 * <pre><code>
 *     {@literal @}Gauge(name = "queueSize")
 *     public int getQueueSize() {
 *         return queue.size;
 *     }
 * </code></pre>
 * A gauge for the defining class with the name {@code queueSize} will be created which uses the
 * annotated method's return value as its value.
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface Gauge {

    /**
     * @return The gauge's name.
     */
    @Nonbinding
    String name() default "";

    /**
     * @return The gauge's tags.
     */
    @Nonbinding
    String[] tags() default {};

    /**
     * @return If {@code true}, use the given name as an absolute name. If {@code false}, use the given name
     * relative to the annotated class.
     */
    @Nonbinding
    boolean absolute() default false;
    
    
    /**
     * 
     * @return display name of the timer from Metadata
     */
    @Nonbinding
    String displayName() default "";
    
    /**
     * 
     * @return description of the timer from Metadata
     */
    @Nonbinding
    String description() default "";
    
    
   /**
    * @return unit of the metrics from Metadata
    *
    */
    @Nonbinding
    String unit();

}
