package com.splinter2.app.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.splinter2.app.Model.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geo on 7/19/14.
 */
public class DBAdapter {

    //Coordinate table
    public static final String KEY_COORDINATE_LOCATION_ID = "location_id";
    public static final String KEY_COORDINATE_LATITUDE = "latitude";
    public static final String KEY_COORDINATE_LONGITUDE = "longitude";
    public static final String KEY_COORDINATE_CREATE_DATE = "create_date";
    public static final String KEY_COORDINATE_MOD_DATE = "mod_date";
    public static final String KEY_COORDINATE_DESCRIPTION = "description";
    public static final String KEY_COORDINATE_ROWID = "_id";

    private static final String TAG = "DBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Table creation sql statement
     */
    private static final String COORDINATE_TABLE_CREATE =
            "create table coordinate (_id integer primary key autoincrement, "
                    + "location_id text null, "
                    + "latitude real null, "
                    + "longitude real null, "
                    + "description text null, "
                    + "create_date integer null, "
                    + "mod_date integer null"
                    + ");";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_COORDINATE = "coordinate";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(COORDINATE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS coordinate");
            onCreate(db);
        }
    }

    public DBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    //COORDINATE TABLE HELPERS
    public long createCoordinate(Coordinate coordinate) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_COORDINATE_LOCATION_ID, coordinate.getLocationId());
        initialValues.put(KEY_COORDINATE_LATITUDE, coordinate.getLatitude());
        initialValues.put(KEY_COORDINATE_LONGITUDE, coordinate.getLongitude());
        initialValues.put(KEY_COORDINATE_DESCRIPTION, coordinate.getDescription());
        initialValues.put(KEY_COORDINATE_CREATE_DATE, coordinate.getCreateTime());
        initialValues.put(KEY_COORDINATE_MOD_DATE, coordinate.getModTime());

        return mDb.insert(DATABASE_TABLE_COORDINATE, null, initialValues);
    }

    public long createCoordinateIfNotExists(Coordinate coordinate) {
        String sql = "INSERT INTO coordinate (location_id, latitude, longitude, description, create_date, mod_date) "
            + "SELECT '" + coordinate.getLocationId() + "', "
            + coordinate.getLatitude() + ", "
            + coordinate.getLongitude() + ", "
            + "'" + coordinate.getDescription() + "', "
            + coordinate.getCreateTime() + ", "
            + coordinate.getModTime() + " "
            + "WHERE NOT EXISTS(SELECT 1 FROM coordinate WHERE location_id = '" + coordinate.getLocationId() + "')";

        Cursor mCursor = mDb.rawQuery(sql, null);

        if (mCursor.moveToFirst()) {
            return mCursor.getLong(0);
        }

        return -1;
    }

    public boolean deleteCoordinate(long rowId) {

        return mDb.delete(DATABASE_TABLE_COORDINATE, KEY_COORDINATE_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteCoordinateByLocationId(long locationId) {

        return mDb.delete(DATABASE_TABLE_COORDINATE, KEY_COORDINATE_LOCATION_ID + "='" + locationId + "'", null) > 0;
    }

    public List<Coordinate> fetchAllCoordinates() {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();

        Cursor mCursor =
                mDb.query(DATABASE_TABLE_COORDINATE, new String[] {
                    KEY_COORDINATE_LOCATION_ID,
                    KEY_COORDINATE_LATITUDE,
                    KEY_COORDINATE_LONGITUDE,
                    KEY_COORDINATE_DESCRIPTION,
                    KEY_COORDINATE_CREATE_DATE,
                    KEY_COORDINATE_MOD_DATE
            }, null, null, null, null, null);

        if (mCursor.moveToFirst()) {
            do {
                Coordinate coordinate = new Coordinate(
                        mCursor.getString(0),
                        Double.parseDouble(mCursor.getString(1)),
                        Double.parseDouble(mCursor.getString(2)),
                        mCursor.getString(3),
                        Long.parseLong(mCursor.getString(4)),
                        Long.parseLong(mCursor.getString(5))
                );

                coordinates.add(coordinate);
            } while (mCursor.moveToNext());
        }

        return coordinates;
    }

    public Coordinate fetchCoordinate(String locationId) throws SQLException {
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE_COORDINATE, new String[] {
                    KEY_COORDINATE_ROWID,
                    KEY_COORDINATE_LOCATION_ID,
                    KEY_COORDINATE_LATITUDE,
                    KEY_COORDINATE_LONGITUDE,
                    KEY_COORDINATE_DESCRIPTION,
                    KEY_COORDINATE_CREATE_DATE,
                    KEY_COORDINATE_MOD_DATE
            }, KEY_COORDINATE_LOCATION_ID + "='" + locationId + "'", null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        Coordinate coordinate = new Coordinate(
                mCursor.getString(0),
                Double.parseDouble(mCursor.getString(1)),
                Double.parseDouble(mCursor.getString(2)),
                mCursor.getString(3),
                Long.parseLong(mCursor.getString(4)),
                Long.parseLong(mCursor.getString(5))
            );

        return coordinate;
    }

    public boolean updateCoordinate(String locationId, Coordinate coordinate) {
        ContentValues args = new ContentValues();
        args.put(KEY_COORDINATE_LOCATION_ID, coordinate.getLocationId());
        args.put(KEY_COORDINATE_LATITUDE, coordinate.getLatitude());
        args.put(KEY_COORDINATE_LONGITUDE, coordinate.getLongitude());
        args.put(KEY_COORDINATE_DESCRIPTION, coordinate.getDescription());
        args.put(KEY_COORDINATE_CREATE_DATE, coordinate.getCreateTime());
        args.put(KEY_COORDINATE_MOD_DATE, coordinate.getModTime());

        return mDb.update(DATABASE_TABLE_COORDINATE, args, KEY_COORDINATE_LOCATION_ID + "='" + locationId + "'", null) > 0;
    }
}
