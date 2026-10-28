package com.calso.robot.application.usecases;

import com.calso.robot.domain.model.CleaningProduct;
import com.calso.robot.domain.model.CleaningProgram;
import com.calso.robot.domain.model.ExecutionResult;
import com.calso.robot.domain.valueobjects.Energy;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExecuteCleaningProgramUseCaseTest {

    // Validaciones del constructor y parámetros
    @Test
    void testGivenNullInventoryWhenCreatingUseCaseThenThrowsException() {
        assertThrows(RuntimeException.class, 
                () -> new ExecuteCleaningProgramUseCase(null));
    }

    @Test
    void testGivenNullProgramWhenExecutingThenThrowsException() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of());
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);
        
        assertThrows(RuntimeException.class, 
                () -> useCase.execute(null));
    }

    // Casos especiales: Sin productos o requisitos a cero
    @Test
    void testGivenProgramWithoutProductsWhenExecutingThenExecutesSuccessfullyWithoutDeductions() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 5));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);
        
        CleaningProgram program = new CleaningProgram("p-1", "Vacío", Energy.of(10), Collections.emptyMap());

        ExecutionResult result = useCase.execute(program);

        assertTrue(result.executed());
        assertTrue(result.missingProducts().isEmpty());
        assertEquals(5, inventory.available(CleaningProduct.WATER)); // Inventario intacto
    }

    @Test
    void testGivenZeroQuantityRequirementsWhenExecutingThenExecutesSuccessfullyWithoutDeductions() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 2));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);
        
        CleaningProgram program = new CleaningProgram("p-2", "Cero", Energy.of(10), Map.of(CleaningProduct.WATER, 0));

        ExecutionResult result = useCase.execute(program);

        assertTrue(result.executed());
        assertTrue(result.missingProducts().isEmpty());
        assertEquals(2, inventory.available(CleaningProduct.WATER)); // Inventario intacto
    }

    // Camino correcto: Hay stock suficiente
    @Test
    void testGivenSufficientStockWhenExecutingThenConsumesInventoryAndReturnsExecutedTrue() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(
                CleaningProduct.WATER, 5,
                CleaningProduct.SOAP, 2
        ));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);
        
        CleaningProgram program = new CleaningProgram("p-3", "Normal", Energy.of(10), Map.of(
                CleaningProduct.WATER, 3,
                CleaningProduct.SOAP, 2 // Disponibilidad exacta
        ));

        ExecutionResult result = useCase.execute(program);

        assertTrue(result.executed());
        assertTrue(result.missingProducts().isEmpty());
        
        // Comprueba los descuentos exactos
        assertEquals(2, inventory.available(CleaningProduct.WATER)); // 5 - 3 = 2
        assertEquals(0, inventory.available(CleaningProduct.SOAP));  // 2 - 2 = 0
    }

    // Camino fallido: Falta stock de algún producto
    @Test
    void testGivenInsufficientStockWhenExecutingThenReturnsFalseWithMissingAndDoesNotModifyInventory() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(
                CleaningProduct.WATER, 5,
                CleaningProduct.SOAP, 1 // Solo hay 1, se necesitan 3
        ));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);
        
        CleaningProgram program = new CleaningProgram("p-4", "Fallo", Energy.of(10), Map.of(
                CleaningProduct.WATER, 2,
                CleaningProduct.SOAP, 3
        ));

        ExecutionResult result = useCase.execute(program);

        assertFalse(result.executed());
        assertEquals(1, result.missingProducts().size());
        assertEquals(2, result.missingProducts().get(CleaningProduct.SOAP)); // Faltan 2 de jabón

        // COMPROBACIÓN CRÍTICA: No debe haber modificado el agua, aunque había suficiente
        assertEquals(5, inventory.available(CleaningProduct.WATER)); 
        assertEquals(1, inventory.available(CleaningProduct.SOAP));  
    }

    @Test
    void testGivenProductNotAvailableAtAllWhenExecutingThenReturnsFalseAndCalculatesMissing() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 10));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);
        
        CleaningProgram program = new CleaningProgram("p-5", "SinStock", Energy.of(10), Map.of(
                CleaningProduct.WAX, 4 // No hay cera en el inventario
        ));

        ExecutionResult result = useCase.execute(program);

        assertFalse(result.executed());
        assertEquals(1, result.missingProducts().size());
        assertEquals(4, result.missingProducts().get(CleaningProduct.WAX)); // Faltan 4
        
        // Inventario intacto
        assertEquals(10, inventory.available(CleaningProduct.WATER));
    }
}