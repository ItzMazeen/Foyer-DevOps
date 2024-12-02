package tn.esprit.spring.services.chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreService implements IChambreService {
    ChambreRepository repo;
    BlocRepository blocRepository;

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return repo.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return repo.findAll();
    }

    @Override
    public Chambre findById(long id) {
        Optional<Chambre> chambre = repo.findById(id);
        return chambre.isPresent() ? chambre.get() : null;
    }


    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        repo.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        return repo.countByTypeCAndBlocIdBloc(type, idBloc);
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        LocalDate[] academicYearRange = getAcademicYearRange();
        LocalDate dateDebutAU = academicYearRange[0];
        LocalDate dateFinAU = academicYearRange[1];

        List<Chambre> listChambreDispo = new ArrayList<>();
        for (Chambre chambre : repo.findAll()) {
            if (isEligibleChambre(chambre, nomFoyer, type) && isChambreAvailable(chambre, type, dateDebutAU, dateFinAU)) {
                listChambreDispo.add(chambre);
            }
        }
        return listChambreDispo;
    }

    private LocalDate[] getAcademicYearRange() {
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        return new LocalDate[]{dateDebutAU, dateFinAU};
    }

    private boolean isEligibleChambre(Chambre chambre, String nomFoyer, TypeChambre type) {
        return chambre.getTypeC().equals(type) && chambre.getBloc().getFoyer().getNomFoyer().equals(nomFoyer);
    }

    private boolean isChambreAvailable(Chambre chambre, TypeChambre type, LocalDate dateDebutAU, LocalDate dateFinAU) {
        int numReservation = (int) chambre.getReservations().stream()
                .filter(reservation -> isWithinAcademicYear(reservation, dateDebutAU, dateFinAU))
                .count();

        return switch (type) {
            case SIMPLE -> numReservation == 0;
            case DOUBLE -> numReservation < 2;
            case TRIPLE -> numReservation < 3;
        };
    }

    private boolean isWithinAcademicYear(Reservation reservation, LocalDate dateDebutAU, LocalDate dateFinAU) {
        return reservation.getAnneeUniversitaire().isBefore(dateFinAU) && reservation.getAnneeUniversitaire().isAfter(dateDebutAU);
    }


    @Override
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() + " ayant une capacité " + b.getCapaciteBloc());

            if (!b.getChambres().isEmpty()) { // Use isEmpty() for readability
                log.info("La liste des chambres pour ce bloc: ");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: " + c.getNumeroChambre() + " type: " + c.getTypeC());
                }
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }

            log.info("********************");
        }
    }


    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = ((double) repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100) / totalChambre;
        double pDouble = ((double) repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100) / totalChambre;
        double pTriple = ((double) repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100) / totalChambre;

        log.info("Nombre total des chambre: " + totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égale à " + pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égale à " + pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égale à " + pTriple);

    }

    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        LocalDate[] academicYearRange = getAcademicYearRange();
        LocalDate dateDebutAU = academicYearRange[0];
        LocalDate dateFinAU = academicYearRange[1];

        for (Chambre chambre : repo.findAll()) {
            long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                    chambre.getIdChambre(), true, dateDebutAU, dateFinAU);

            logAvailablePlaces(chambre, nbReservation);
        }
    }



    private void logAvailablePlaces(Chambre chambre, long nbReservation) {
        int maxCapacity = getMaxCapacityByType(chambre.getTypeC());
        int availablePlaces = maxCapacity - (int) nbReservation;

        if (availablePlaces > 0) {
            log.info("Le nombre de place disponible pour la chambre " + chambre.getTypeC() + " " + chambre.getNumeroChambre() + " est " + availablePlaces);
        } else {
            log.info("La chambre " + chambre.getTypeC() + " " + chambre.getNumeroChambre() + " est complete");
        }
    }

    private int getMaxCapacityByType(TypeChambre type) {
        return switch (type) {
            case SIMPLE -> 1;
            case DOUBLE -> 2;
            case TRIPLE -> 3;
        };
    }

}
