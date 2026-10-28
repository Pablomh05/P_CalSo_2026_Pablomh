package com.calso.robot.application.usecases;

import java.util.EnumMap;
import java.util.Map;
import com.calso.robot.domain.model.CleaningProduct;
import com.calso.robot.domain.repositories.ICleaningInventory;

/**
 * Suplantador de inventario para pruebas. IMPLEMENTACIÓN PENDIENTE.
 * La implementación debe mantenerse exclusivamente bajo src/test/java.
 */
public final class StubCleaningInventory implements ICleaningInventory {
	
	private final Map<CleaningProduct, Integer> stock;

    public StubCleaningInventory(Map<CleaningProduct, Integer> initialStock) {
    	if (initialStock == null) {
            throw new IllegalArgumentException("El stock inicial no puede ser nulo");
        }
        
        // Copia defensiva y validación del estado inicial
        this.stock = new EnumMap<>(CleaningProduct.class);
        for (Map.Entry<CleaningProduct, Integer> entry : initialStock.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() < 0) {
                throw new IllegalArgumentException("Clave/Valor nulo o cantidad negativa en el stock inicial");
            }
            this.stock.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public int available(CleaningProduct product) {
    	if (product == null) {
            return 0;
        }
        // Devuelve existencia actual o cero si el producto no está presente
        return stock.getOrDefault(product, 0);
    }

    @Override
    public void consume(CleaningProduct product, int quantity) {
    	if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("La cantidad a consumir no puede ser negativa");
        }
        
        int currentStock = available(product);
        if (quantity > currentStock) {
            // Lanza excepción sin modificar el inventario, garantizando atomicidad
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + product);
        }
        
        // Descuenta exactamente quantity unidades
        stock.put(product, currentStock - quantity);
    }
}
