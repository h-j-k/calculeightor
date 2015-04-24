package com.ikueb.calculeightor;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Operator implements BinaryOperator<Double> {
    ADD {
        @Override
        public Double apply(Double t, Double u) {
            return Double.valueOf(t.doubleValue() + u.doubleValue());
        }

        @Override
        public String toString() {
            return "+";
        }
    },
    SUBTRACT {
        @Override
        public Double apply(Double t, Double u) {
            return Double.valueOf(t.doubleValue() - u.doubleValue());
        }

        @Override
        public String toString() {
            return "-";
        }
    },
    MULTIPLY {
        @Override
        public Double apply(Double t, Double u) {
            return Double.valueOf(t.doubleValue() * u.doubleValue());
        }

        @Override
        public String toString() {
            return "*";
        }
    },
    DIVIDE {
        @Override
        public Double apply(Double t, Double u) {
            return Double.valueOf(t.doubleValue() / u.doubleValue());
        }

        @Override
        public String toString() {
            return "/";
        }
    },
    MODULUS {
        @Override
        public Double apply(Double t, Double u) {
            return Double.valueOf(t.intValue() % u.intValue());
        }

        @Override
        public String toString() {
            return "%";
        }
    },
    EXPONENT {
        @Override
        public Double apply(Double t, Double u) {
            return Double.valueOf(Math.pow(t.doubleValue(), u.doubleValue()));
        }

        @Override
        public String toString() {
            return "^";
        }
    };

    static final Map<String, Operator> SYMBOLS = Stream.of(values()).collect(
            Collectors.toMap(Object::toString, Function.identity()));

    static Operator of(String operator) {
        return SYMBOLS.get(operator);
    }
}
