package com.ikueb.calculeightor;

import java.util.function.BinaryOperator;
import java.util.stream.Stream;

/**
 * A lightweight interface for describing how values can be appended into a stream and
 * then reduced to a single result given an operator.
 *
 * @param <T>
 */
public interface Calculeightor<T extends Number> {

    /**
     * @param value the value to add
     */
    void appendValue(T value);

    Stream<T> getValues();

    /**
     * @param operator the operator to use
     */
    void setOperator(BinaryOperator<T> operator);

    BinaryOperator<T> getOperator();

    /**
     * @return the result
     */
    default T getResult() {
        return getValues().reduce(getOperator()).get();
    }

    /**
     * @param value the value to display
     * @return a formatted {@link String} without trailing decimals if possible
     */
    default String display(T value) {
        return value.doubleValue() % 1 == 0 ? Integer.toString(value.intValue())
                : value.toString();
    }
}
