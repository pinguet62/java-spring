package fr.pinguet62.junit5.composition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

public class CombinationSourcesTest {
    @Nested
    class ShouldExecuteEachValueByEachSource {

        static List<List<Object>> callsArgs = new ArrayList<>();

        @ParameterizedTest
        @CombinationSources({
                @CombinationSource(
                        valueSource = @ValueSource(strings = {"Scooter", "Car"}),
                        emptySource = @EmptySource),
                @CombinationSource(
                        valueSource = @ValueSource(ints = {14, 16}),
                        nullSource = @NullSource),
                @CombinationSource(
                        valueSource = @ValueSource(booleans = {false, true})),
        })
        void test(String vehicle, Integer age, boolean sex) {
            callsArgs.add(asList(vehicle, age, sex));
        }

        @AfterAll
        static void verify() {
            // matrix
            assertThat(callsArgs, hasSize((2 + 1) * (2 + 1) * 2));

            // support @ValueSource
            assertThat(callsArgs, hasItems(
                    List.of("Scooter", 14, false),
                    List.of("Car", 14, false),
                    List.of("Scooter", 16, false),
                    List.of("Scooter", 14, true)));
            // support @EmptySource
            assertThat(callsArgs, hasItems(
                    List.of("", 14, false),
                    List.of("", 16, false),
                    List.of("", 14, true)));
            // support @NullSource
            assertThat(callsArgs, hasItems(
                    asList("Scooter", null, false),
                    asList("Car", null, false),
                    asList("Scooter", null, true)));
        }
    }
}
