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
