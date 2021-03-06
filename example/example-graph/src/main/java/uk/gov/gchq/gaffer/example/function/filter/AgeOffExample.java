/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package uk.gov.gchq.gaffer.example.function.filter;

import uk.gov.gchq.gaffer.function.filter.AgeOff;

public class AgeOffExample extends FilterFunctionExample {
    private long now = System.currentTimeMillis();

    public static void main(final String[] args) {
        new AgeOffExample().run();
    }

    public AgeOffExample() {
        super(AgeOff.class);
    }

    public void runExamples() {
        ageOffInMilliseconds();
    }

    public void ageOffInMilliseconds() {
        // ---------------------------------------------------------
        final AgeOff function = new AgeOff(100000L);
        // ---------------------------------------------------------

        runExample(function, "", now, now - 100000L, now + 100000L, String.valueOf(now));
    }
}
