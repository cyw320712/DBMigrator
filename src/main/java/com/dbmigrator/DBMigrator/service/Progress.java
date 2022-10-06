package com.dbmigrator.DBMigrator.service;

import java.util.*;

public class Progress {
    private Boolean value;

    public Progress(){
        value = false;
    }

    public Progress(Boolean values) {
        value = true;
    }

    public Boolean getValue(){
        return value;
    }
}
