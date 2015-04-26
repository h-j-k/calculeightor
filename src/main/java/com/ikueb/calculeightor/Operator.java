package com.ikueb.calculeightor;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Operator implements BinaryOperator<Double> {
    ADD("+", (t, u) -> Double.valueOf(t.doubleValue() + u.doubleValue())),
    SUBTRACT("-", (t, u) -> Double.valueOf(t.doubleValue() - u.doubleValue())),
    MULTIPLY("*", (t, u) -> Double.valueOf(t.doubleValue() * u.doubleValue())),
    DIVIDE("/", (t, u) -> Double.valueOf(t.doubleValue() / u.doubleValue())),
    MODULUS("%", (t, u) -> Double.valueOf(t.intValue() % u.intValue())),
    EXPONENT("^", (t, u) -> Double.valueOf(Math.pow(t.doubleValue(), u.doubleValue())));

    private final String toString;
    private final BinaryOperator<Double> operator;

    private Operator(String toString, BinaryOperator<Double> operator) {
        this.toString = toString;
        this.operator = operator;
    }

    @Override
    public Double apply(Double t, Double u) {
        return operator.apply(t, u);
    }

    @Override
    public String toString() {
        return toString;
    }

    private static final Map<String, Operator> SYMBOLS = Stream.of(values()).collect(
            Collectors.toMap(Object::toString, Function.identity()));

    public static Operator of(String operator) {
        return SYMBOLS.get(operator);
    }
}
