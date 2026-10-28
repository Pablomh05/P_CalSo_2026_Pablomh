package com.calso.robot.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionResultTest {

    // executed == true con missingProducts vacío
    @Test
    void testGivenExecutedTrueAndEmptyMapWhenCreatingResultThenExecutedIsTrueAndMissingProductsIsEmpty() {
        ExecutionResult result = new ExecutionResult(true, Collections.emptyMap());

        assertTrue(result.executed());
        assertTrue(result.missingProducts().isEmpty());
    }

    // Si executed == false, missingProducts contiene los productos insuficientes y sus cantidades
    @Test
    void testGivenExecutedFalseAndMissingProductsWhenCreatingResultThenStoresMissingProducts() {
        Map<CleaningProduct, Integer> missing = Map.of(
                CleaningProduct.WATER, 2,
                CleaningProduct.SOAP, 1
        );

        ExecutionResult result = new ExecutionResult(false, missing);

        assertFalse(result.executed());
        assertEquals(missing, result.missingProducts());
    }

    // Validación de mapa nulo
    @Test
    void testGivenNullMissingProductsWhenCreatingResultThenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new ExecutionResult(false, null));
    }

    // El mapa devuelto no se puede modificar externamente y hace copia defensiva
    @Test
    void testGivenResultWhenModifyingReturnedMissingProductsMapThenThrowsUnsupportedOperationException() {
        Map<CleaningProduct, Integer> mutableInput = new EnumMap<>(CleaningProduct.class);
        mutableInput.put(CleaningProduct.WATER, 2);

        ExecutionResult result = new ExecutionResult(false, mutableInput);
        Map<CleaningProduct, Integer> returnedMap = result.missingProducts();

        assertThrows(UnsupportedOperationException.class,
                () -> returnedMap.put(CleaningProduct.WAX, 1));
    }

    @Test
    void testGivenMutableMapWhenModifiedAfterCreationThenMissingProductsRemainsUnchanged() {
        Map<CleaningProduct, Integer> originalMap = new EnumMap<>(CleaningProduct.class);
        originalMap.put(CleaningProduct.WATER, 2);

        ExecutionResult result = new ExecutionResult(false, originalMap);

        originalMap.put(CleaningProduct.SOAP, 5);
        originalMap.put(CleaningProduct.WATER, 99);

        assertEquals(1, result.missingProducts().size());
        assertEquals(2, result.missingProducts().get(CleaningProduct.WATER));
    }
}