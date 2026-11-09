package es.um.calso.dojoreservation.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import es.um.calso.dojoreservation.domain.model.Discount;
import es.um.calso.dojoreservation.domain.model.Fighter;
import es.um.calso.dojoreservation.domain.model.UnemploymentPeriod;

class ReservationDiscountServiceTest {

    private final ReservationDiscountService service = new ReservationDiscountService();
    private final LocalDate refDate = LocalDate.of(2026, 10, 21);
    
    // Método auxiliar para crear luchadores fácilmente en los tests
    private Fighter createFighter(String name, LocalDate birthDate, UnemploymentPeriod period) {
        return new Fighter(name, birthDate, period);
    }

    // 3. Tests para findYoungestFighter (Caminos C17 a C23)

    @Test
    void testGivenFightersNullWhenFindYoungestThenThrowsException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.findYoungestFighter(null, refDate);
        });
        assertEquals("Fighters are required", exception.getMessage());
    }

    @Test
    void testGivenRefDateNullWhenFindYoungestThenThrowsException() {
        List<Fighter> fighters = List.of();
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.findYoungestFighter(fighters, null);
        });
        assertEquals("Reference date is required", exception.getMessage());
    }

    @Test
    void testGivenEmptyFightersWhenFindYoungestThenReturnsEmpty() {
        List<Fighter> fighters = List.of();
        Optional<Fighter> result = service.findYoungestFighter(fighters, refDate);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGivenNullFighterWhenFindYoungestThenThrowsException() {
        // En Java, List.of() no admite null, usamos ArrayList
        List<Fighter> fighters = new java.util.ArrayList<>();
        fighters.add(null);
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.findYoungestFighter(fighters, refDate);
        });
        assertEquals("Null fighter", exception.getMessage());
    }

    @Test
    void testGivenOneFighterWhenFindYoungestThenReturnsFighter() {
        Fighter f1 = createFighter("F1", LocalDate.of(2000, 1, 1), null);
        List<Fighter> fighters = List.of(f1);
        
        Optional<Fighter> result = service.findYoungestFighter(fighters, refDate);
        
        assertTrue(result.isPresent());
        assertEquals("F1", result.get().name()); // <-- Cambiado a name()
    }

    @Test
    void testGivenSecondYoungerWhenFindYoungestThenReturnsSecond() {
        Fighter f1 = createFighter("F1", LocalDate.of(1990, 1, 1), null);
        Fighter f2 = createFighter("F2", LocalDate.of(2000, 1, 1), null);
        List<Fighter> fighters = List.of(f1, f2);
        
        Optional<Fighter> result = service.findYoungestFighter(fighters, refDate);
        
        // Fallará por el bug de isBefore
        assertTrue(result.isPresent());
        assertEquals("F2", result.get().name());
    }

    @Test
    void testGivenSecondOlderWhenFindYoungestThenReturnsFirst() {
        Fighter f1 = createFighter("F1", LocalDate.of(2000, 1, 1), null);
        Fighter f2 = createFighter("F2", LocalDate.of(1990, 1, 1), null);
        List<Fighter> fighters = List.of(f1, f2);
        
        Optional<Fighter> result = service.findYoungestFighter(fighters, refDate);
        
        // Fallará por el bug de isBefore
        assertTrue(result.isPresent());
        assertEquals("F1", result.get().name());
    }

    // 4. Tests para calculateBestDiscount (Caminos C24 a C35)

    @Test
    void testGivenFightersNullWhenCalculateDiscountThenThrowsException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.calculateBestDiscount(null, refDate);
        });
        assertEquals("Fighters are required", exception.getMessage());
    }

    @Test
    void testGivenDateNullWhenCalculateDiscountThenThrowsException() {
        List<Fighter> fighters = List.of();
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.calculateBestDiscount(fighters, null);
        });
        assertEquals("Training date is required", exception.getMessage());
    }

    @Test
    void testGivenEmptyFightersWhenCalculateDiscountThenReturnsZero() {
        List<Fighter> fighters = List.of();
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        assertEquals(0, discount.percentage());
    }

    @Test
    void testGivenAdultNoUnemploymentWhenCalculateThenReturnsZero() {
        // Adulto de 20 años en 2026
        Fighter f1 = createFighter("F1", LocalDate.of(2006, 1, 1), null);
        List<Fighter> fighters = List.of(f1);
        
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        assertEquals(0, discount.percentage());
    }

    @Test
    void testGivenMinorNoUnemploymentWhenCalculateThenReturnsFifteen() {
        // Menor de 16 años en 2026
        Fighter f1 = createFighter("F1", LocalDate.of(2010, 1, 1), null);
        List<Fighter> fighters = List.of(f1);
        
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        assertEquals(15, discount.percentage());
    }

    @Test
    void testGivenAdultUnemploymentUnderOneYearThenReturnsZero() {
        // Paro de medio año (6 meses antes de refDate)
        UnemploymentPeriod period = new UnemploymentPeriod(refDate.minusMonths(6));
        Fighter f1 = createFighter("F1", LocalDate.of(2000, 1, 1), period);
        List<Fighter> fighters = List.of(f1);
        
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        assertEquals(0, discount.percentage());
    }

    @Test
    void testGivenAdultUnemploymentOneYearThenReturnsTen() {
        // Paro de exactamente 1 año
        UnemploymentPeriod period = new UnemploymentPeriod(refDate.minusYears(1));
        Fighter f1 = createFighter("F1", LocalDate.of(2000, 1, 1), period);
        List<Fighter> fighters = List.of(f1);
        
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        assertEquals(10, discount.percentage());
    }

    @Test
    void testGivenAdultUnemploymentTwoYearsThenReturnsTwenty() {
        // Paro de exactamente 2 años
        UnemploymentPeriod period = new UnemploymentPeriod(refDate.minusYears(2));
        Fighter f1 = createFighter("F1", LocalDate.of(2000, 1, 1), period);
        List<Fighter> fighters = List.of(f1);
        
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        
        // Fallará por el bug del > 2 en lugar de >= 2
        assertEquals(20, discount.percentage());
    }

    @Test
    void testGivenAdultUnemploymentOverTwoYearsThenReturnsTwenty() {
        // Paro de 2.5 años
        UnemploymentPeriod period = new UnemploymentPeriod(refDate.minusYears(2).minusMonths(6));
        Fighter f1 = createFighter("F1", LocalDate.of(2000, 1, 1), period);
        List<Fighter> fighters = List.of(f1);
        
        // Fallará por el bug del > 2 en lugar de >= 2
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        assertEquals(20, discount.percentage());
    }

    @Test
    void testGivenAdultUnemploymentThreeYearsThenReturnsThirty() {
        // Paro de exactamente 3 años
        UnemploymentPeriod period = new UnemploymentPeriod(refDate.minusYears(3));
        Fighter f1 = createFighter("F1", LocalDate.of(2000, 1, 1), period);
        List<Fighter> fighters = List.of(f1);
        
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        assertEquals(30, discount.percentage());
    }

    @Test
    void testGivenMinorUnemploymentOneYearThenReturnsFifteen() {
        // Menor (17 años, nacido en 2009) con paro de 1 año. Gana el 15% de menor frente al 10%.
        UnemploymentPeriod period = new UnemploymentPeriod(refDate.minusYears(1));
        Fighter f1 = createFighter("F1", LocalDate.of(2009, 1, 1), period);
        List<Fighter> fighters = List.of(f1);
        
        Discount discount = service.calculateBestDiscount(fighters, refDate);
        assertEquals(15, discount.percentage());
    }
}