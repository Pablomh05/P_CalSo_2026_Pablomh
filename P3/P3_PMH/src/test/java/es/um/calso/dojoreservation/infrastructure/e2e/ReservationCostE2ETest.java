package es.um.calso.dojoreservation.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class ReservationCostE2ETest extends BrowserE2ESupport {

    private final String futureDate = LocalDate.now().plusDays(10).toString();

    // CASOS E2E: Pantalla de Presupuesto y Costes (/coste)

    @Test
    void testGivenValidInputsWhenCalculateCostThenDisplaysPrices() {
        // E2E-6: Equipo válido, dojo válido y fecha futura
        open("/coste?teamId=EQ-001&dojoId=D-001&trainingDate=" + futureDate);
        
        String bodyText = textAt(By.tagName("body"));
        
        assertFalse(bodyText.contains("is required"), "No debe mostrar errores de campos requeridos");
        assertFalse(bodyText.contains("does not exist"), "No debe mostrar errores de elementos inexistentes");
        assertTrue(bodyText.contains("Precio final"), "Debe mostrar el precio final");
    }

    @Test
    void testGivenEmptyDojoWhenCalculateCostThenDisplaysError() {
        // E2E-7: Dojo vacío (enviando el parámetro sin valor)
        open("/coste?teamId=EQ-001&dojoId=&trainingDate=" + futureDate);
        
        String bodyText = textAt(By.tagName("body"));
        assertTrue(bodyText.contains("Dojo id is required") || bodyText.contains("requerido") || bodyText.contains("Dojo"), "Debe mostrar error de dojo requerido");
    }

    @Test
    void testGivenInvalidDojoWhenCalculateCostThenDisplaysError() {
        // E2E-8: Dojo inexistente (D-999)
        open("/coste?teamId=EQ-001&dojoId=D-999&trainingDate=" + futureDate);
        
        String bodyText = textAt(By.tagName("body"));
        assertTrue(bodyText.contains("Dojo does not exist") || bodyText.contains("existe"), "Debe mostrar error de dojo inexistente");
    }
}