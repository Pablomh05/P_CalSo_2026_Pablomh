package com.calso.robot.domain.model;
import java.util.Map;
import java.util.Objects;
import com.calso.robot.domain.valueobjects.Energy;

public final class CleaningProgram {
    private final String id;
    private final String name;
    private final Energy energy;
    private final Map<CleaningProduct,Integer> requirements;
    
    public CleaningProgram(String id, String name, Energy energy, Map<CleaningProduct,Integer> requirements) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) 
            throw new IllegalArgumentException("Invalid name or id");
        
        this.energy = Objects.requireNonNull(energy, "energy");
        
        Objects.requireNonNull(requirements, "requirements");
        
        if (requirements.entrySet().stream().anyMatch(e -> e.getKey() == null || e.getValue() == null || e.getValue() < 0)) 
            throw new IllegalArgumentException("Invalid requirements");
        
        this.id = id; 
        this.name = name; 
        // CORRECCIÓN D-PR-1: Copia defensiva e inmodificable
        this.requirements = Map.copyOf(requirements);
    }
    
    public String getId() { 
        return id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public Energy getEnergy() { 
        return energy; 
    }
    
    public Map<CleaningProduct,Integer> getRequirements() { 
        return requirements; 
    }
    
    @Override
    public boolean equals(Object other) {
        // CORRECCIÓN D-PR-2: Igualdad basada únicamente en id
        return other instanceof CleaningProgram p && id.equals(p.id);
    }
    
    @Override
    public int hashCode() { 
        // CORRECCIÓN D-PR-2: HashCode basado únicamente en id
        return id.hashCode(); 
    }
}
