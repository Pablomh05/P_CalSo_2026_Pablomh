package es.um.calso.dojoreservation.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Money;
import es.um.calso.dojoreservation.domain.model.Reservation;
import es.um.calso.dojoreservation.domain.model.Weapon;

class DojoSearchServiceTest {

    private final DojoSearchService service = new DojoSearchService();
    private final LocalDate date = LocalDate.of(2026, 10, 21);
    // Creamos un precio ficticio para esquivar el validaciones de nulos del constructor
    private final Money dummyPrice = new Money(BigDecimal.TEN);

    // 1. Tests para containsAllRequiredWeapons (Caminos C1 a C5)

    @Test
    void testGivenAvailableNullWhenContainsAllThenThrowsException() {
        Set<Weapon> required = Set.of();
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.containsAllRequiredWeapons(null, required);
        });
        
        assertEquals("Available weapons are required", exception.getMessage());
    }

    @Test
    void testGivenRequiredNullWhenContainsAllThenThrowsException() {
        Set<Weapon> available = Set.of();
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.containsAllRequiredWeapons(available, null);
        });
        
        assertEquals("Required weapons are required", exception.getMessage());
    }

    @Test
    void testGivenRequiredEmptyWhenContainsAllThenReturnsTrue() {
        Set<Weapon> available = Set.of(new Weapon("Bokken"));
        Set<Weapon> required = Set.of();
        
        boolean result = service.containsAllRequiredWeapons(available, required);
        
        assertTrue(result);
    }

    @Test
    void testGivenAvailableEmptyWhenContainsAllThenReturnsFalse() {
        Set<Weapon> available = Set.of();
        Set<Weapon> required = Set.of(new Weapon("Katana"));
        
        boolean result = service.containsAllRequiredWeapons(available, required);
        
        assertFalse(result);
    }

    @Test
    void testGivenMissingWeaponWhenContainsAllThenReturnsFalse() {
        Set<Weapon> available = Set.of(new Weapon("Bokken"));
        Set<Weapon> required = Set.of(new Weapon("Katana"));
        
        boolean result = service.containsAllRequiredWeapons(available, required);
        
        // Este test FALLARÁ hasta que corrija el bug en DojoSearchService.
        assertFalse(result); 
    }

    // 2. Tests para findFirstAvailableDojo (Caminos C6 a C16)

    @Test
    void testGivenDojosNullWhenFindFirstThenThrowsException() {
        Set<Weapon> reqWeapons = Set.of();
        List<Reservation> res = List.of();
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.findFirstAvailableDojo(null, reqWeapons, res, date);
        });
        
        assertEquals("Dojos are required", exception.getMessage());
    }

    @Test
    void testGivenWeaponsNullWhenFindFirstThenThrowsException() {
        List<Dojo> dojos = List.of();
        List<Reservation> res = List.of();
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.findFirstAvailableDojo(dojos, null, res, date);
        });
        
        assertEquals("Required weapons are required", exception.getMessage());
    }

    @Test
    void testGivenReservationsNullWhenFindFirstThenThrowsException() {
        List<Dojo> dojos = List.of();
        Set<Weapon> reqWeapons = Set.of();
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.findFirstAvailableDojo(dojos, reqWeapons, null, date);
        });
        
        assertEquals("Reservations are required", exception.getMessage());
    }

    @Test
    void testGivenDateNullWhenFindFirstThenThrowsException() {
        List<Dojo> dojos = List.of();
        Set<Weapon> reqWeapons = Set.of();
        List<Reservation> res = List.of();
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            service.findFirstAvailableDojo(dojos, reqWeapons, res, null);
        });
        
        assertEquals("Training date is required", exception.getMessage());
    }

    @Test
    void testGivenEmptyDojosWhenFindFirstThenReturnsEmpty() {
        List<Dojo> dojos = List.of();
        Set<Weapon> reqWeapons = Set.of();
        List<Reservation> res = List.of();
        
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, reqWeapons, res, date);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGivenDojoWithoutWeaponsWhenFindFirstThenReturnsEmpty() {
        Dojo d1 = new Dojo("D1", "Dojo 1", dummyPrice, Set.of());
        List<Dojo> dojos = List.of(d1);
        Set<Weapon> reqWeapons = Set.of(new Weapon("Katana"));
        List<Reservation> res = List.of();
        
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, reqWeapons, res, date);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGivenDojoOkAndNoReservationsWhenFindFirstThenReturnsDojo() {
        Weapon katana = new Weapon("Katana");
        Dojo d1 = new Dojo("D1", "Dojo 1", dummyPrice, Set.of(katana));
        List<Dojo> dojos = List.of(d1);
        Set<Weapon> reqWeapons = Set.of(katana);
        List<Reservation> res = List.of();
        
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, reqWeapons, res, date);
        
        assertTrue(result.isPresent());
        assertEquals("D1", result.get().id());
    }

    @Test
    void testGivenDojoOkAndReservationForOtherWhenFindFirstThenReturnsDojo() {
        Weapon katana = new Weapon("Katana");
        Dojo d1 = new Dojo("D1", "Dojo 1", dummyPrice, Set.of(katana));
        List<Dojo> dojos = List.of(d1);
        Set<Weapon> reqWeapons = Set.of(katana);
        List<Reservation> res = List.of(new Reservation("D2", date));
        
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, reqWeapons, res, date);
        
        assertTrue(result.isPresent());
        assertEquals("D1", result.get().id());
    }

    @Test
    void testGivenDojoOkAndReservationSameDateWhenFindFirstThenReturnsEmpty() {
        Weapon katana = new Weapon("Katana");
        Dojo d1 = new Dojo("D1", "Dojo 1", dummyPrice, Set.of(katana));
        List<Dojo> dojos = List.of(d1);
        Set<Weapon> reqWeapons = Set.of(katana);
        List<Reservation> res = List.of(new Reservation("D1", date));
        
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, reqWeapons, res, date);
        
        // Este test FALLARÁ hasta que corrija el bug (!equals) en DojoSearchService.
        assertTrue(result.isEmpty());
    }

    @Test
    void testGivenDojoOkAndReservationDiffDateWhenFindFirstThenReturnsDojo() {
        Weapon katana = new Weapon("Katana");
        Dojo d1 = new Dojo("D1", "Dojo 1", dummyPrice, Set.of(katana));
        List<Dojo> dojos = List.of(d1);
        Set<Weapon> reqWeapons = Set.of(katana);
        List<Reservation> res = List.of(new Reservation("D1", date.plusDays(1)));
        
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, reqWeapons, res, date);
        
        // Este test FALLARÁ hasta que corrija el bug (!equals) en DojoSearchService.
        assertTrue(result.isPresent());
        assertEquals("D1", result.get().id());
    }

    @Test
    void testGivenFirstDojoOccupiedReturnsSecondDojo() {
        Weapon katana = new Weapon("Katana");
        Dojo d1 = new Dojo("D1", "Dojo 1", dummyPrice, Set.of(katana));
        Dojo d2 = new Dojo("D2", "Dojo 2", dummyPrice, Set.of(katana));
        List<Dojo> dojos = List.of(d1, d2);
        Set<Weapon> reqWeapons = Set.of(katana);
        List<Reservation> res = List.of(new Reservation("D1", date));
        
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, reqWeapons, res, date);
        
        // Este test FALLARÁ hasta que corrija el bug (!equals) en DojoSearchService.
        assertTrue(result.isPresent());
        assertEquals("D2", result.get().id());
    }
}