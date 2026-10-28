package com.calso.robot.domain.model;

import com.calso.robot.domain.valueobjects.Energy;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CleaningProgramTest {

    // Validaciones del constructor: id y name (no nulos ni cadena blanca)
    @Test
    void testGivenNullIdWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Energy energy = Energy.of(10);
        Map<CleaningProduct, Integer> emptyReqs = Collections.emptyMap();

        assertThrows(IllegalArgumentException.class,
                () -> new CleaningProgram(null, "Nombre", energy, emptyReqs));
    }

    @Test
    void testGivenBlankIdWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Energy energy = Energy.of(10);
        Map<CleaningProduct, Integer> emptyReqs = Collections.emptyMap();

        assertThrows(IllegalArgumentException.class,
                () -> new CleaningProgram("   ", "Nombre", energy, emptyReqs));
    }

    @Test
    void testGivenNullNameWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Energy energy = Energy.of(10);
        Map<CleaningProduct, Integer> validReqs = Map.of(CleaningProduct.WATER, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new CleaningProgram("id-1", null, energy, validReqs));
    }

    @Test
    void testGivenBlankNameWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Energy energy = Energy.of(10);
        Map<CleaningProduct, Integer> validReqs = Map.of(CleaningProduct.WATER, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new CleaningProgram("id-1", "   ", energy, validReqs));
    }

    // Validaciones del constructor: energy y requirements (no nulos)
    @Test
    void testGivenNullEnergyWhenCreatingProgramThenThrowsNullPointerException() {
        Map<CleaningProduct, Integer> emptyReqs = Map.of();

        assertThrows(NullPointerException.class,
                () -> new CleaningProgram("id-1", "Nombre", null, emptyReqs));
    }

    @Test
    void testGivenNullRequirementsWhenCreatingProgramThenThrowsNullPointerException() {
        Energy energy = Energy.of(10);

        assertThrows(NullPointerException.class,
                () -> new CleaningProgram("id-1", "Nombre", energy, null));
    }

    // Validaciones de las entradas del mapa: claves/cantidades no nulas y cantidades >= 0
    @Test
    void testGivenNullKeyInRequirementsWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Energy energy = Energy.of(10);
        Map<CleaningProduct, Integer> reqsWithNullKey = new HashMap<>();
        reqsWithNullKey.put(null, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new CleaningProgram("id-1", "Nombre", energy, reqsWithNullKey));
    }

    @Test
    void testGivenNullValueInRequirementsWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Energy energy = Energy.of(10);
        Map<CleaningProduct, Integer> reqsWithNullValue = new EnumMap<>(CleaningProduct.class);
        reqsWithNullValue.put(CleaningProduct.WATER, null);

        assertThrows(IllegalArgumentException.class,
                () -> new CleaningProgram("id-1", "Nombre", energy, reqsWithNullValue));
    }

    @Test
    void testGivenNegativeQuantityInRequirementsWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Energy energy = Energy.of(10);
        Map<CleaningProduct, Integer> reqs = Map.of(CleaningProduct.WATER, -1);

        assertThrows(IllegalArgumentException.class,
                () -> new CleaningProgram("id-1", "Nombre", energy, reqs));
    }

    // El mapa puede estar vacío y no se limita la cantidad de productos
    @Test
    void testGivenEmptyRequirementsWhenCreatingProgramThenProgramIsCreatedWithEmptyMap() {
        CleaningProgram program = new CleaningProgram("prog-1", "Seco", Energy.of(5), Collections.emptyMap());
        assertTrue(program.getRequirements().isEmpty());
    }

    // getId(), getName(), getEnergy(), getRequirements() devuelven las propiedades correspondientes (incluyendo cantidad 0 válida)
    @Test
    void testGivenValidArgumentsWhenCreatingProgramThenGettersReturnExpectedValues() {
        Energy energy = Energy.of(15);
        Map<CleaningProduct, Integer> reqs = Map.of(
                CleaningProduct.WATER, 2,
                CleaningProduct.SOAP, 0
        );

        CleaningProgram program = new CleaningProgram("prog-1", "Limpieza General", energy, reqs);

        assertEquals("prog-1", program.getId());
        assertEquals("Limpieza General", program.getName());
        assertEquals(energy, program.getEnergy());
        assertEquals(reqs, program.getRequirements());
    }

    // Copia defensiva al construir y mapa de lectura no modificable al consultar
    @Test
    void testGivenMutableMapWhenModifiedAfterCreationThenProgramRequirementsRemainUnchanged() {
        Map<CleaningProduct, Integer> originalMap = new EnumMap<>(CleaningProduct.class);
        originalMap.put(CleaningProduct.WATER, 2);

        CleaningProgram program = new CleaningProgram("id-1", "Nombre", Energy.of(10), originalMap);

        originalMap.put(CleaningProduct.SOAP, 5);
        originalMap.put(CleaningProduct.WATER, 99);

        assertEquals(1, program.getRequirements().size());
        assertEquals(2, program.getRequirements().get(CleaningProduct.WATER));
    }

    @Test
    void testGivenProgramWhenModifyingReturnedRequirementsMapThenThrowsUnsupportedOperationException() {
        Map<CleaningProduct, Integer> mutableInput = new EnumMap<>(CleaningProduct.class);
        mutableInput.put(CleaningProduct.WATER, 2);

        CleaningProgram program = new CleaningProgram("id-1", "Nombre", Energy.of(10), mutableInput);
        Map<CleaningProduct, Integer> returnedMap = program.getRequirements();

        assertThrows(UnsupportedOperationException.class,
                () -> returnedMap.put(CleaningProduct.WAX, 1));
    }

    // Igualdad (equals/hashCode) basada únicamente en id
    @Test
    void testGivenTwoProgramsWithSameIdAndDifferentAttributesWhenComparingThenTheyAreEqualAndSameHashCode() {
        CleaningProgram prog1 = new CleaningProgram(
                "mismo-id", "Nombre A", Energy.of(10), Map.of(CleaningProduct.WATER, 1));
        CleaningProgram prog2 = new CleaningProgram(
                "mismo-id", "Nombre B", Energy.of(50), Map.of(CleaningProduct.WAX, 5));

        assertEquals(prog1, prog2);
        assertEquals(prog1.hashCode(), prog2.hashCode());
    }

    @Test
    void testGivenTwoProgramsWithDifferentIdAndSameAttributesWhenComparingThenTheyAreNotEqual() {
        CleaningProgram prog1 = new CleaningProgram(
                "id-1", "Mismo Nombre", Energy.of(10), Map.of(CleaningProduct.WATER, 1));
        CleaningProgram prog2 = new CleaningProgram(
                "id-2", "Mismo Nombre", Energy.of(10), Map.of(CleaningProduct.WATER, 1));

        assertNotEquals(prog1, prog2);
        assertNotEquals(prog1.hashCode(), prog2.hashCode());
    }
}