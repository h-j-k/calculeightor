/*
 * Copyright 2015 h-j-k. All Rights Reserved.
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
package com.ikueb.calculeightor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CalculeightorCommandLineInterfaceTest {

    enum TestCase {
        EXIT,
        INVALID_INPUTS("abc", "0.1", "0.0", "/", "%"),
        ONE_INTEGER("0"),
        ONE_INTEGER_AND_OPERATOR("0", "+"),
        ADD(Arrays.asList("6"), "1", "2", "3", "+"),
        SUBTRACT(Arrays.asList("-1"), "4", "5", "-"),
        MULTIPLY(Arrays.asList("42"), "6", "7", "*"),
        EXPONENT(Arrays.asList("134217728"), "8", "9", "^"),
        MULTI(Arrays.asList("21", "156"), "10", "11", "+", "-12", "-13", "*");

        private final List<String> expected;
        private final String[] inputs;

        private TestCase(String... inputs) {
            this(Collections.emptyList(), inputs);
        }

        private TestCase(List<String> expected, String... inputs) {
            this.expected = expected;
            this.inputs = inputs;
        }

        void doTest() {
            OutputStream out = new ByteArrayOutputStream();
            new CalculeightorCLI().calculate(toInputStream(inputs), out);
            assertThat(parseOutput(out), equalTo(expected));
        }

        /**
         * @param inputs the inputs to use
         * @return an {@link InputStream} of the inputs
         */
        private static InputStream toInputStream(String... inputs) {
            return new ByteArrayInputStream(Arrays.stream(inputs)
                    .collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8));
        }

        /**
         * @param out the output to parse
         * @return the {@link List} of calculation results
         */
        private static List<String> parseOutput(OutputStream out) {
            int prefix = CalculeightorCLI.RESULT_PREFIX.length();
            return Pattern.compile("\\v+").splitAsStream(out.toString().trim())
                    .map(i -> i.substring(prefix)).collect(Collectors.toList());
        }
    }

    @DataProvider(name = "test-cases")
    public Iterator<Object[]> getTestCases() {
        return Stream.of(TestCase.values()).map(t -> new Object[] { t }).iterator();
    }

    @Test(dataProvider = "test-cases")
    public void test(TestCase testCase) {
        testCase.doTest();
    }
}
