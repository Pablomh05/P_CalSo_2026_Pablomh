package com.calso.robot.application.usecases;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import com.calso.robot.application.ports.inbound.IExecuteCleaningProgram;
import com.calso.robot.domain.model.CleaningProduct;
import com.calso.robot.domain.model.CleaningProgram;
import com.calso.robot.domain.model.ExecutionResult;
import com.calso.robot.domain.repositories.ICleaningInventory;

public final class ExecuteCleaningProgramUseCase implements IExecuteCleaningProgram {
    
    private final ICleaningInventory inventory;
    
    public ExecuteCleaningProgramUseCase(ICleaningInventory inventory) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
    }
    
    @Override
    public ExecutionResult execute(CleaningProgram program) {
        Objects.requireNonNull(program, "program");
        Map<CleaningProduct, Integer> missing = new EnumMap<>(CleaningProduct.class);
        
        // Comprobar disponibilidad de todos los productos (sin descontar nada)
        for (var requirement : program.getRequirements().entrySet()) {
            CleaningProduct product = requirement.getKey();
            int required = requirement.getValue();
            int available = inventory.available(product);
            
            // CORRECCIÓN D-UC-1 y D-UC-2: available < required, y calcular diferencia
            if (available < required) {
                missing.put(product, required - available);
            }
        }
        
        // Si falta algún producto, abortamos sin modificar inventario
        if (!missing.isEmpty()) {
            return new ExecutionResult(false, missing);
        }
        
        // Si hay stock suficiente, procedemos con los descuentos
        for (var requirement : program.getRequirements().entrySet()) {
            int required = requirement.getValue();
            if (required > 0) {
                inventory.consume(requirement.getKey(), required);
            }
        }
        
        return new ExecutionResult(true, missing); // missing está vacío llegados a este punto
    }
}
