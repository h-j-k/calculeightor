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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The CLI implementation of {@link Calculeightor}.
 * <p>
 * There is no limitation placed on the number of inputs before entering the operator to
 * use.
 */
public class CalculeightorCLI implements Calculeightor<Double> {

    public static final Map<String, Operator> OPERATORS = getOperators();
    public static final String RESULT_PREFIX = "Result: ";
    public static final String END_INPUT = "e";
    private static final int MIN_VALUES = 2;

    private final List<Double> inputs = new ArrayList<>();
    private BinaryOperator<Double> operator;

    private static Map<String, Operator> getOperators() {
        return EnumSet.complementOf(EnumSet.of(Operator.DIVIDE, Operator.MODULUS))
                .stream()
                .collect(Collectors.toMap(Object::toString, Function.identity()));
    }

    public void calculate(InputStream source, OutputStream out) {
        try (Scanner scanner = new Scanner(source);
                @SuppressWarnings("resource")
                PrintStream printer = new PrintStream(out)) {
            while (scanner.hasNext()) {
                String input = scanner.next().trim();
                if (endInput(input)) {
                    return;
                } else if (isOperator(input)) {
                    if (acceptOperator(input)) {
                        setOperator(OPERATORS.get(input));
                        showOutput(printer);
                    }
                } else {
                    acceptValue(input);
                }
            }
        }
    }

    private boolean endInput(String input) {
        return input.endsWith(END_INPUT);
    }

    private boolean isOperator(String input) {
        return OPERATORS.containsKey(input);
    }

    private boolean acceptOperator(String input) {
        if (inputs.size() < MIN_VALUES) {
            System.err.printf("Minimum %d integers before calculation.%n",
                    Integer.valueOf(MIN_VALUES));
            return false;
        }
        return true;
    }

    private void acceptValue(String input) {
        try {
            appendValue(Double.valueOf(Integer.parseInt(input)));
        } catch (NumberFormatException e) {
            System.err.println("Not an integer, ignored: " + input);
        }
    }

    /**
     * Writes the result onto the {@link OutputStream} via a {@link PrintStream}, and
     * then clear the inputs.
     *
     * @param out the {@link OutputStream} to use
     */
    private void showOutput(PrintStream out) {
        if (operator == null) {
            return;
        }
        out.println(RESULT_PREFIX + display(getResult()));
        inputs.clear();
    }

    @Override
    public void appendValue(Double value) {
        inputs.add(value);
    }

    @Override
    public Stream<Double> getValues() {
        return inputs.stream();
    }

    @Override
    public void setOperator(BinaryOperator<Double> operator) {
        this.operator = operator;
    }

    @Override
    public BinaryOperator<Double> getOperator() {
        return operator;
    }

    public static void main(String[] args) {
        System.out.printf("Enter integers, any of %s as operators or '%s' to exit.%n",
                OPERATORS.keySet(), END_INPUT);
        new CalculeightorCLI().calculate(System.in, System.out);
    }

}
