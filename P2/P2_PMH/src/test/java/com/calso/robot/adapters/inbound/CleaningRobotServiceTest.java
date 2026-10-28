package com.calso.robot.adapters.inbound;

import com.calso.robot.application.usecases.StubCleaningInventory;
import com.calso.robot.domain.model.CleaningProduct;
import com.calso.robot.domain.model.CleaningProgram;
import com.calso.robot.domain.model.ExecutionResult;
import com.calso.robot.domain.valueobjects.Energy;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CleaningRobotServiceTest {

    @Test
    void testGivenSufficientStockWhenExecutingProgramThenDelegatesProperlyAndConsumes() {
        // Escenario 1: Inventario: agua=2; programa: agua=2 -> ejecuta, inventario final agua=0.
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 2));
        CleaningRobotService service = new CleaningRobotService(inventory);

        CleaningProgram program = new CleaningProgram("p-1", "Test1", Energy.of(5), Map.of(CleaningProduct.WATER, 2));

        ExecutionResult result = service.executeProgram(program);

        assertTrue(result.executed());
        assertTrue(result.missingProducts().isEmpty());
        assertEquals(0, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenInsufficientStockWhenExecutingProgramThenDelegatesAndLeavesInventoryIntact() {
        // Escenario 2: Inventario: agua=2, jabón=0; programa: agua=2, jabón=1 -> no ejecuta, faltante jabón=1, inventario intacto.
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(
                CleaningProduct.WATER, 2,
                CleaningProduct.SOAP, 0
        ));
        CleaningRobotService service = new CleaningRobotService(inventory);

        CleaningProgram program = new CleaningProgram("p-2", "Test2", Energy.of(5), Map.of(
                CleaningProduct.WATER, 2,
                CleaningProduct.SOAP, 1
        ));

        ExecutionResult result = service.executeProgram(program);

        assertFalse(result.executed());
        assertEquals(1, result.missingProducts().size());
        assertEquals(1, result.missingProducts().get(CleaningProduct.SOAP));
        
        // El inventario debe quedar intacto por la transaccionalidad del caso de uso
        assertEquals(2, inventory.available(CleaningProduct.WATER));
    }
}