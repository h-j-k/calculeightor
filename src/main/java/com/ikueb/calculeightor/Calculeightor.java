package com.ikueb.calculeightor;

import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public interface Calculeightor<T extends Number> {

    void appendValue(T value);

    Stream<T> getValues();

    void setOperator(BinaryOperator<T> operator);

    BinaryOperator<T> getOperator();

    default T getResult() {
        return getValues().reduce(getOperator()).get();
    }
}
