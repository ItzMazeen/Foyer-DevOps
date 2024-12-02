package Services.Universite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import tn.esprit.spring.FoyerApplication;
import tn.esprit.spring.dao.entities.Universite;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoyerApplication.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UniversiteServiceTest {

    @InjectMocks
    private UniversiteService universiteService;

    @Mock
    private UniversiteRepository universiteRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addOrUpdate_ShouldSaveUniversite_AndVerifySavedData() {
        // Arrange
        Universite universite = new Universite();
        universite.setNomUniversite("ESPRIT");
        universite.setAdresse("Tunis");

        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        // Act
        Universite savedUniversite = universiteService.addOrUpdate(universite);

        // Assert
        assertNotNull(savedUniversite);
        assertEquals("ESPRIT", savedUniversite.getNomUniversite());

        // Advanced: Capture the actual Universite object passed to the repository save method
        ArgumentCaptor<Universite> captor = ArgumentCaptor.forClass(Universite.class);
        verify(universiteRepository, times(1)).save(captor.capture());

        Universite capturedUniversite = captor.getValue();
        assertEquals("ESPRIT", capturedUniversite.getNomUniversite());
        assertEquals("Tunis", capturedUniversite.getAdresse());
    }

    @Test
    void findAll_ShouldReturnAllUniversites_WithMultipleRecords() {
        // Arrange
        Universite universite1 = new Universite();
        universite1.setNomUniversite("ESPRIT");
        Universite universite2 = new Universite();
        universite2.setNomUniversite("ENIT");

        when(universiteRepository.findAll()).thenReturn(Arrays.asList(universite1, universite2));

        // Act
        List<Universite> universites = universiteService.findAll();

        // Assert
        assertEquals(2, universites.size());
        assertTrue(universites.stream().anyMatch(u -> "ESPRIT".equals(u.getNomUniversite())));
        assertTrue(universites.stream().anyMatch(u -> "ENIT".equals(u.getNomUniversite())));
    }

    @Test
    void findById_ShouldThrowException_WhenUniversiteNotFound() {
        // Arrange
        long id = 99L; // Non-existent ID

        when(universiteRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> universiteService.findById(id));
        assertEquals("Universite with ID " + id + " not found", exception.getMessage());
    }

    @Test
    void deleteById_ShouldThrowException_WhenUniversiteNotFound() {
        // Arrange
        long id = 99L; // Non-existent ID

        doThrow(new IllegalArgumentException("Universite with ID " + id + " not found")).when(universiteRepository).deleteById(id);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> universiteService.deleteById(id));
        assertEquals("Universite with ID " + id + " not found", exception.getMessage());
    }

    @Test
    void addOrUpdate_ShouldUpdateExistingUniversite() {
        // Arrange
        Universite existingUniversite = new Universite();
        existingUniversite.setIdUniversite(1L);
        existingUniversite.setNomUniversite("Old Name");

        Universite updatedUniversite = new Universite();
        updatedUniversite.setIdUniversite(1L);
        updatedUniversite.setNomUniversite("New Name");

        when(universiteRepository.findById(1L)).thenReturn(Optional.of(existingUniversite));
        when(universiteRepository.save(updatedUniversite)).thenReturn(updatedUniversite);

        // Act
        Universite result = universiteService.addOrUpdate(updatedUniversite);

        // Assert
        assertEquals("New Name", result.getNomUniversite());
        verify(universiteRepository, times(1)).save(updatedUniversite);
    }

    @Test
    void delete_ShouldVerifyDeleteCall_WithCorrectObject() {
        // Arrange
        Universite universite = new Universite();
        universite.setIdUniversite(1L);
        universite.setNomUniversite("ESPRIT");

        // Act
        universiteService.delete(universite);

        // Assert
        ArgumentCaptor<Universite> captor = ArgumentCaptor.forClass(Universite.class);
        verify(universiteRepository, times(1)).delete(captor.capture());

        Universite capturedUniversite = captor.getValue();
        assertEquals("ESPRIT", capturedUniversite.getNomUniversite());
        assertEquals(1L, capturedUniversite.getIdUniversite());
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoUniversitesExist() {
        // Arrange
        when(universiteRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Universite> universites = universiteService.findAll();

        // Assert
        assertNotNull(universites);
        assertTrue(universites.isEmpty());
    }
}
