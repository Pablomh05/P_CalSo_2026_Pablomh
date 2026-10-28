package com.calso.robot.application.usecases.resources;

import com.calso.robot.domain.model.CleaningProgram;
import com.calso.robot.domain.valueobjects.Energy;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CleaningProgramByEnergyComparatorTest {

    private final CleaningProgramByEnergyComparator comparator = new CleaningProgramByEnergyComparator();

    @Test
    void testGivenDifferentEnergyWhenComparingThenSortsByEnergyAscending() {
        CleaningProgram lowerEnergy = new CleaningProgram("id-1", "A", Energy.of(10), Collections.emptyMap());
        CleaningProgram higherEnergy = new CleaningProgram("id-2", "B", Energy.of(20), Collections.emptyMap());

        // lowerEnergy es menor que higherEnergy (debe devolver negativo)
        assertTrue(comparator.compare(lowerEnergy, higherEnergy) < 0);
        // higherEnergy es mayor que lowerEnergy (debe devolver positivo)
        assertTrue(comparator.compare(higherEnergy, lowerEnergy) > 0);
    }

    @Test
    void testGivenSameEnergyAndDifferentIdWhenComparingThenSortsByIdAscending() {
        // Misma energía, desempatamos por ID alfabético ("a-id" va antes que "b-id")
        CleaningProgram lowerId = new CleaningProgram("a-id", "A", Energy.of(15), Collections.emptyMap());
        CleaningProgram higherId = new CleaningProgram("b-id", "B", Energy.of(15), Collections.emptyMap());

        assertTrue(comparator.compare(lowerId, higherId) < 0);
        assertTrue(comparator.compare(higherId, lowerId) > 0);
    }

    @Test
    void testGivenSameEnergyAndSameIdWhenComparingThenReturnsZero() {
        // Mismo ID y misma energía
        CleaningProgram p1 = new CleaningProgram("id-same", "Nombre 1", Energy.of(10), Collections.emptyMap());
        CleaningProgram p2 = new CleaningProgram("id-same", "Nombre 2", Energy.of(10), Collections.emptyMap());

        assertEquals(0, comparator.compare(p1, p2));
    }
}