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

public class CalculeightorCLI implements Calculeightor<Double> {

    private static final String END_INPUT = "e";
    private static final int MIN_VALUES = 2;
    private static final Map<String, Operator> OPERATORS = getOperators();

    private final List<Double> inputs = new ArrayList<>();
    private BinaryOperator<Double> operator;

    private static Map<String, Operator> getOperators() {
        return EnumSet.complementOf(EnumSet.of(Operator.DIVIDE, Operator.MODULUS))
                .stream()
                .collect(Collectors.toMap(Object::toString, Function.identity()));
    }

    @SuppressWarnings("resource")
    private void calculate(InputStream source, OutputStream out) {
        try (Scanner scanner = new Scanner(source);
                PrintStream printer = new PrintStream(out)) {
            while (scanner.hasNext()) {
                String input = scanner.next().trim();
                if (input.equals(END_INPUT)) {
                    return;
                } else if (OPERATORS.containsKey(input)) {
                    if (inputs.size() < MIN_VALUES) {
                        System.err.printf("Minimum %d integers before calculation.",
                                Integer.valueOf(MIN_VALUES));
                        continue;
                    }
                    setOperator(OPERATORS.get(input));
                    showOutput(printer);
                } else {
                    try {
                        appendValue(Double.valueOf(Integer.parseInt(input)));
                    } catch (NumberFormatException e) {
                        System.err.println("Not an integer, ignored: " + input);
                    }
                }
            }
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
        out.println("Result: " + display(getResult()));
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
        System.out.printf("Enter integers, any of %s as operators or '%s' to exit.",
                OPERATORS.keySet(), END_INPUT);
        new CalculeightorCLI().calculate(System.in, System.out);
    }

}
