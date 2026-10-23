package com.calso.robot.domain.valueobjects;

import java.util.Objects;

public final class Energy {
    private final int units;
    
    private Energy(int units) { 
        this.units = units; 
    }
    
    public static Energy of(int units) {
    	// CORRECCIÓN 1: La especificación exige estrictamente > 0
        if (units <= 0) 
            throw new IllegalArgumentException("Energy must be positive");
        return new Energy(units);
    }
    
    public int getValue() { 
        return units; 
    }
    
 // CORRECCIÓN 2: Comparación real por el valor de 'units'
    @Override
    public boolean equals(Object other) { 
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Energy energy = (Energy) other;
        return units == energy.units;
    }
    
    // CORRECCIÓN 3: HashCode basado en el valor, no en la memoria
    @Override
    public int hashCode() { 
        return Objects.hash(units); 
    }
}
