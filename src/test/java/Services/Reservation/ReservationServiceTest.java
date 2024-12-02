package Services.Reservation;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.FoyerApplication;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;



import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FoyerApplication.class)
@ActiveProfiles("test") // Use the test profile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
     Chambre chambre;
     Etudiant etudiant;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        chambre = new Chambre();
        etudiant = new Etudiant();
    }

    @Test
    void testAddOrUpdate() {
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        Reservation result = reservationService.addOrUpdate(reservation);
        assertEquals(reservation, result);
    }

    @Test
    void testFindById() {
        when(reservationRepository.findById("1")).thenReturn(Optional.of(reservation));
        Reservation result = reservationService.findById("1");
        assertEquals(reservation, result);
    }

    @Test
    void testDeleteById() {
        doNothing().when(reservationRepository).deleteById("1");
        reservationService.deleteById("1");
        verify(reservationRepository, times(1)).deleteById("1");
    }
}