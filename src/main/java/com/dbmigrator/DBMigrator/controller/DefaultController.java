package com.dbmigrator.DBMigrator.controller;

import com.dbmigrator.DBMigrator.service.MigratorService;
import com.dbmigrator.DBMigrator.service.Progress;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DefaultController {

    private final MigratorService migratorService;

    @GetMapping("/")
    public String migrate() throws InterruptedException {
        List<Progress> resultList =  migratorService.migrate(null);
        return "Complete";
    }

}
