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
