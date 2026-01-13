package com.asistencia.servidor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaControleer {

    @Autowired
    private RegistroRepository repository;

    @PostMapping
    public Registro guardarRegistro(@RequestBody Registro nuevo) {
        return repository.save(nuevo);
    }

    @GetMapping
    public List<Registro> obtenerTodos() {
        return repository.findAll();
    }
}