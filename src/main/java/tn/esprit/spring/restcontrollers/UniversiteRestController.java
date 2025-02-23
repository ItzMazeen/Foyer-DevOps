package tn.esprit.spring.restcontrollers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.services.universite.IUniversiteService;

import java.util.List;

@RestController
@RequestMapping("universite")
@AllArgsConstructor
@CrossOrigin(origins = "http://192.168.56.18:4200")

public class UniversiteRestController {
    IUniversiteService service;

    @PostMapping("addOrUpdate")
    Universite addOrUpdate(@RequestBody Universite u) {
        return service.addOrUpdate(u);
    }

    @GetMapping("findAll")
    List<Universite> findAll() {
        return service.findAll();
    }

    @GetMapping("findById")
    Universite findById(@RequestParam long id) {
        return service.findById(id);
    }

    @DeleteMapping("delete")
    void delete(@RequestBody Universite u) {
        service.delete(u);
    }

    @DeleteMapping("deleteById")
    void deleteById(@RequestParam long id) {
        service.deleteById(id);
    }


}
