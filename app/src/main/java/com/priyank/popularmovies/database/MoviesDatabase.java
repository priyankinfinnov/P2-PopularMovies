package com.priyank.popularmovies.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by akash on 21/2/16.
 */
@Database(version = MoviesDatabase.VERSION)
public class MoviesDatabase {

    private MoviesDatabase(){}

    public static final int VERSION = 1;

    @Table(MoviesColumns.class) public static final String MOVIES = "movies";

}
