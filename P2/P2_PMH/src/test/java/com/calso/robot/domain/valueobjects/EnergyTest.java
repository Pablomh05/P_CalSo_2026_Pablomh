package com.calso.robot.domain.valueobjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnergyTest {

    @Test
    void testGivenEnergy1WhenCreatingEnergyThenStoresValue1() {
        Energy energy = Energy.of(1);
        assertEquals(1, energy.getValue());
    }

    @Test
    void testGivenEnergy0WhenCreatingEnergyThenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Energy.of(0));
    }

    @Test
    void testGivenEnergyMinus5WhenCreatingEnergyThenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Energy.of(-5));
    }

    @Test
    void testGivenTwoEnergyValues10WhenComparingThenTheyAreEqualAndHaveSameHashCode() {
        Energy first = Energy.of(10);
        Energy second = Energy.of(10);
        
        assertEquals(first, second); 
        
        assertEquals(first.hashCode(), second.hashCode()); 
    }
}