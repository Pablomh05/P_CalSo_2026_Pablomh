package es.um.calso.dojoreservation.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class DojoSearchE2ETest extends BrowserE2ESupport {

    private final String futureDate = LocalDate.now().plusDays(10).toString();
    private final String todayDate = LocalDate.now().toString();
    private final String pastDate = LocalDate.now().minusDays(5).toString();

    // CASOS E2E: Pantalla de Búsqueda (/dojos)

    @Test
    void testGivenValidTeamAndFutureDateWhenSearchThenDisplaysDojos() {
        // E2E-1: Caso válido completo
    	open("/dojos?teamId=EQ-001&trainingDate=" + futureDate);
    	
    	String bodyText = textAt(By.tagName("body"));
        assertFalse(bodyText.contains("is required"), "No debe mostrar error de equipo");
        assertFalse(bodyText.contains("Training date must be in the future"), "No debe mostrar error de fecha");
        assertTrue(bodyText.contains("D-001") || bodyText.contains("Dojo Norte"), "Debe mostrar los dojos");
    }

    @Test
    void testGivenEmptyTeamWhenSearchThenDisplaysError() {
        // E2E-2: Equipo vacío (opción por defecto). Tras corregir la UI, usamos la navegación estándar.
    	open("/dojos?trainingDate=" + futureDate);
    	
        String bodyText = textAt(By.tagName("body"));
        assertTrue(bodyText.contains("Team id is required"), "Debe mostrar error de equipo vacío");
    }

    @Test
    void testGivenInvalidTeamWhenSearchThenDisplaysError() {
        // E2E-3: Equipo no válido
    	open("/dojos?teamId=EQ-999&trainingDate=" + futureDate);
    	
        String bodyText = textAt(By.tagName("body"));
        assertTrue(bodyText.contains("Team does not exist"), "Debe mostrar error de equipo inexistente");
    }

    @Test
    void testGivenPastDateWhenSearchThenDisplaysError() {
        // E2E-4: Fecha pasada
    	open("/dojos?teamId=EQ-001&trainingDate=" + pastDate);
    	
        String bodyText = textAt(By.tagName("body"));
        assertTrue(bodyText.contains("Training date must be in the future"), "Debe mostrar error de fecha");
    }

    @Test
    void testGivenTodayDateWhenSearchThenDisplaysError() {
        // E2E-5: Fecha de hoy
    	open("/dojos?teamId=EQ-001&trainingDate=" + todayDate);
    	
        String bodyText = textAt(By.tagName("body"));
        assertTrue(bodyText.contains("Training date must be in the future"), "Debe mostrar error de fecha");
    }
}