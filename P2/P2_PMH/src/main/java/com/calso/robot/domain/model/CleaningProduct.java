package com.calso.robot.domain.model;

public enum CleaningProduct {
    // CORRECCIÓN: Cambiado "cristales" por "limpiacristales"
    WATER("agua"), SOAP("jabón"), WAX("cera"), GLASS_CLEANER("limpiacristales");
    
    private final String label;
    
    CleaningProduct(String label) { 
        this.label = label; 
    }
    
    @Override
    public String toString() { 
        return label; 
    }
}