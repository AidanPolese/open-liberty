/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
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
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.ws.microprofile.health.spi.impl;

import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.spi.HealthCheckResponseProvider;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.health.impl.HealthCheckResponseBuilderImpl;

/**
 * Health
 */
public class HealthCheckResponseProviderImpl implements HealthCheckResponseProvider {

    private static final TraceComponent tc = Tr.register(HealthCheckResponseProviderImpl.class);

    public HealthCheckResponseProviderImpl() {}

    @Override
    public HealthCheckResponseBuilder createResponseBuilder() {
        HealthCheckResponseBuilder builder = new HealthCheckResponseBuilderImpl();

        return builder;
    }

}
