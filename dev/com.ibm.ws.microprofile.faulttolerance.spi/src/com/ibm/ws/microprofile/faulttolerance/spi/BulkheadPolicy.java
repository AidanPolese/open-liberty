/*
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
 */
package com.ibm.ws.microprofile.faulttolerance.spi;

import java.time.Duration;

/**
 * Wrap the execution and invoke it, limiting the number of threads used.
 */
public interface BulkheadPolicy {
    /**
     * The maximum number of threads which may execute the method at any one time.
     *
     * @return the maximum number of threads to use.
     *
     */
    public int getMaxThreads();

    public void setMaxThreads(int maxThreads);

    public Duration getTimeout();

    public void setTimeout(Duration timeout);
}
