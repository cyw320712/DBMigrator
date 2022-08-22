package com.dbmigrater.DBMigrater.controller;

import com.dbmigrater.DBMigrater.service.MigratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DefaultController {

    private final MigratorService migratorService;

    @GetMapping()
    public String migrate() {
        return migratorService.migrate();
    }

}
