package fr.pinguet62.junit5.composition;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class CombinationArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<CombinationSources> {

    private CombinationSources combinationSources;

    @Override
    public void accept(CombinationSources matrixValueSource) {
        this.combinationSources = matrixValueSource;
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        List<Stream<? extends Arguments>> argumentProviders = new ArrayList<>();
        for (CombinationSource combinationSource : combinationSources.value()) {
            Stream<? extends Arguments> paramArguments = Stream.empty();

            for (ValueSource source : combinationSource.valueSource()) {
                Class<? extends ArgumentsProvider> argumentsProviderClass = (Class<? extends ArgumentsProvider>) Class.forName("org.junit.jupiter.params.provider.ValueArgumentsProvider");
                ArgumentsProvider argumentsProvider = ReflectionUtils.newInstance(argumentsProviderClass);
                ((AnnotationConsumer<ValueSource>) argumentsProvider).accept(source);
                paramArguments = Stream.concat(paramArguments, argumentsProvider.provideArguments(extensionContext));
            }
            for (EmptySource source : combinationSource.emptySource()) {
                Class<? extends ArgumentsProvider> argumentsProviderClass = (Class<? extends ArgumentsProvider>) Class.forName("org.junit.jupiter.params.provider.EmptyArgumentsProvider");
                ArgumentsProvider argumentsProvider = ReflectionUtils.newInstance(argumentsProviderClass);
                paramArguments = Stream.concat(paramArguments, argumentsProvider.provideArguments(extensionContext));
            }
            for (NullSource source : combinationSource.nullSource()) {
                Class<? extends ArgumentsProvider> argumentsProviderClass = (Class<? extends ArgumentsProvider>) Class.forName("org.junit.jupiter.params.provider.NullArgumentsProvider");
                ArgumentsProvider argumentsProvider = ReflectionUtils.newInstance(argumentsProviderClass);
                paramArguments = Stream.concat(paramArguments, argumentsProvider.provideArguments(extensionContext));
            }
            for (EnumSource source : combinationSource.enumSource()) {
                Class<? extends ArgumentsProvider> argumentsProviderClass = (Class<? extends ArgumentsProvider>) Class.forName("org.junit.jupiter.params.provider.EnumArgumentsProvider");
                ArgumentsProvider argumentsProvider = ReflectionUtils.newInstance(argumentsProviderClass);
                paramArguments = Stream.concat(paramArguments, argumentsProvider.provideArguments(extensionContext));
            }

            argumentProviders.add(paramArguments);
        }

        List<List<Object>> providersArguments = argumentProviders.stream()
                .map(argumentProvider -> argumentProvider.collect(toList()))
                .map(providerArguments -> providerArguments.stream()
                        .map(arguments -> arguments.get()[0])
                        .collect(toList()))
                .collect(toList());

        List<List<Object>> combination = combinator(providersArguments);
        return combination.stream()
                .map(it -> Arguments.of(it.toArray()));
    }

    public static <T> List<List<T>> combinator(List<List<T>> arguments) {
        if (arguments.size() == 1) {
            return arguments.get(0).stream()
                    .map(Arrays::asList)
                    .collect(toList());
        }

        List<List<T>> result = new ArrayList<>();
        List<List<T>> nextCombinations = combinator(arguments.subList(1, arguments.size()));
        for (T value : arguments.get(0)) {
            for (List<T> nextCombination : nextCombinations) {
                List<T> currentCombination = new ArrayList<>();
                currentCombination.add(value);
                currentCombination.addAll(nextCombination);
                result.add(currentCombination);
            }
        }
        return result;
    }
}
