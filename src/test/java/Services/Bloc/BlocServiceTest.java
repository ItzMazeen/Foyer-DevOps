package Services.Bloc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Foyer;



import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BlocServiceTest {

    @InjectMocks
    private BlocService blocService;

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private FoyerRepository foyerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>());
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        Bloc result = blocService.addOrUpdate(bloc);

        assertNotNull(result);
        assertEquals("Bloc A", result.getNomBloc());
        verify(blocRepository, times(1)).save(bloc);
    }

    @Test
    void testFindAll() {
        List<Bloc> blocs = Arrays.asList(new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>()));
        when(blocRepository.findAll()).thenReturn(blocs);

        List<Bloc> result = blocService.findAll();

        assertEquals(1, result.size());
        assertEquals("Bloc A", result.get(0).getNomBloc());
        verify(blocRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        when(blocRepository.findAll()).thenReturn(new ArrayList<>());

        List<Bloc> result = blocService.findAll();

        assertTrue(result.isEmpty());
        verify(blocRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>());
        when(blocRepository.findById(anyLong())).thenReturn(Optional.of(bloc));

        Bloc result = blocService.findById(1L);

        assertNotNull(result);
        assertEquals("Bloc A", result.getNomBloc());
        verify(blocRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(blocRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            blocService.findById(999L); // Assuming 999 does not exist
        });

        assertTrue(exception.getMessage().contains("No value present for Bloc with ID: 999"));
        verify(blocRepository, times(1)).findById(999L);
    }


    @Test
    void testDeleteById() {
        blocService.deleteById(1L);

        verify(blocRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        Chambre chambre1 = new Chambre();
        Chambre chambre2 = new Chambre();
        List<Chambre> chambres = Arrays.asList(chambre1, chambre2);
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, chambres);

        blocService.delete(bloc);

        verify(chambreRepository, times(2)).delete(any(Chambre.class));
        verify(blocRepository, times(1)).delete(bloc);
    }


    @Test
    void testAffecterChambresABloc() {
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>());
        when(blocRepository.findByNomBloc(anyString())).thenReturn(bloc);
        when(chambreRepository.findByNumeroChambre(anyLong())).thenReturn(new Chambre());

        Bloc result = blocService.affecterChambresABloc(Arrays.asList(1L, 2L), "Bloc A");

        assertNotNull(result);
        verify(chambreRepository, times(2)).findByNumeroChambre(anyLong());
        verify(chambreRepository, times(2)).save(any(Chambre.class));
    }

    @Test
    void testAffecterChambresABloc_BlocNotFound() {
        String nomBloc = "NonExistentBloc";
        List<Long> numChambres = List.of(1L, 2L);

        Mockito.when(blocRepository.findByNomBloc(nomBloc)).thenReturn(null); // Simulate not finding the bloc

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            blocService.affecterChambresABloc(numChambres, nomBloc);
        });

        assertTrue(exception.getMessage().contains("Bloc not found"));
    }

    @Test
    void testAffecterBlocAFoyer() {
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>());
        Foyer foyer = new Foyer();
        when(blocRepository.findByNomBloc(anyString())).thenReturn(bloc);
        when(foyerRepository.findByNomFoyer(anyString())).thenReturn(foyer);
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        Bloc result = blocService.affecterBlocAFoyer("Bloc A", "Foyer A");

        assertNotNull(result);
        verify(blocRepository, times(1)).save(bloc);
    }

}