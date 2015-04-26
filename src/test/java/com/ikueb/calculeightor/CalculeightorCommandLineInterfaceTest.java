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
        ONE_INTEGER("1"),
        ONE_INTEGER_AND_OPERATOR("0", "+"),
        ADD(Arrays.asList("3"), "1", "2", "+"),
        SUBTRACT(Arrays.asList("-1"), "3", "4", "-"),
        MULTIPLY(Arrays.asList("30"), "5", "6", "*"),
        EXPONENT(Arrays.asList("5764801"), "7", "8", "^"),
        MULTI(Arrays.asList("19", "132"), "9", "10", "+", "-11", "-12", "*");

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
