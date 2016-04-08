package com.priyank.popularmovies.database;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by akash on 21/2/16.
 */
public class MoviesColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    public static final String _ID = "_id";
    public static final int COL_ID = 0;

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";
    public static final int COL_TITLE = 1;

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String OVERVIEW = "overview";
    public static final int COL_OVERVIEW = 2;

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RATING = "rating";
    public static final int COL_RATING = 3;

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String DATE = "date";
    public static final int COL_DATE = 4;

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String POSTER = "poster";
    public static final int COL_POSTER = 5;

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String BACKDROP = "backdrop";
    public static final int COL_BACKDROP = 6;
}
