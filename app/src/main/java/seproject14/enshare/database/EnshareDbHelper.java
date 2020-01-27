package seproject14.enshare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnshareDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "EnShareApp|EnshareDbHelper";
    private static final int DB_VERSION = 5;
    private static final String DB_NAME = "enshare_db";
    private static final String DB_TABLE = "enshare_imgmsg";
    private static final String DB_COLUMN_ID = "id";
    private static final String DB_COLUMN_TYPE = "type";
    private static final String DB_COLUMN_DATE = "date";
    private static final String DB_COLUMN_LAT = "lat";
    private static final String DB_COLUMN_LON = "lon";
    private static final String DB_COLUMN_MSG = "msg";
    private static final String DB_COLUMN_USER = "user";

    public static final int DB_SORT_DATE_ASC = 0;
    public static final int DB_SORT_DATE_DESC = 1;
    public static final int DB_SORT_LOCATION_ASC = 2;
    public static final int DB_SORT_LOCATION_DESC = 3;

    private static final String SQL_CREATE_TABLE =
            String.format("CREATE TABLE %s ("
                + "%s INTEGER PRIMARY KEY,"
                + "%s INTEGER,"
                + "%s DATETIME,"
                + "%s DOUBLE,"
                + "%s DOUBLE,"
                + "%s VARCHAR,"
                + "%s VARCHAR"
            + ")",
                    DB_TABLE,
                    DB_COLUMN_ID,
                    DB_COLUMN_TYPE,
                    DB_COLUMN_DATE,
                    DB_COLUMN_LAT,
                    DB_COLUMN_LON,
                    DB_COLUMN_MSG,
                    DB_COLUMN_USER);

    private static final String SQL_DELETE_TABLE =
            String.format("DROP TABLE IF EXISTS %s", DB_TABLE);

    private static final String[] SQL_MIGRATIONS = {
            /* 0 -> 1 */ String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s VARCHAR)",
                DB_TABLE, DB_COLUMN_ID, DB_COLUMN_MSG),
            /* 1 -> 2 */ String.format("ALTER TABLE %s ADD COLUMN %s INTEGER",
                DB_TABLE, DB_COLUMN_TYPE),
            /* 2 -> 3 */ String.format("ALTER TABLE %s ADD COLUMN %s DATETIME",
                DB_TABLE, DB_COLUMN_DATE),
            /* 3 -> 4 */ String.format("ALTER TABLE %s ADD COLUMN %s DOUBLE",
                DB_TABLE, DB_COLUMN_LAT),
            /* 4 -> 5 */ String.format("ALTER TABLE %s ADD COLUMN %s DOUBLE",
                DB_TABLE, DB_COLUMN_LON),
            /* 5 -> 6 */ String.format("ALTER TABLE %s ADD COLUMN %s VARCHAR",
                DB_TABLE, DB_COLUMN_USER),
    };

    public EnshareDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "create database");

        // generate the database schema
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("upgrade '%s' to '%s'", oldVersion, newVersion));

        // execute all migration queries
        for (int i = oldVersion; i < newVersion && i < SQL_MIGRATIONS.length; ++i) {
            Log.d(TAG, String.format("migrating %s -> %s", i, i + 1));
            db.execSQL(SQL_MIGRATIONS[i]);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("downgrade '%s' to '%s'", oldVersion, newVersion));

        // I am afraid we will not handle this case.
        // For the moment, we will just reinitialize
        // the database, but this is of course a
        // destructive operation, so we might want
        // to have the consent of the user.
        db.execSQL(SQL_DELETE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    public long insert(long type, Date date, double lat, double lon, String message, String user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_TYPE, type);
        values.put(DB_COLUMN_DATE, date.getTime() / 1000l); // convert to seconds
        values.put(DB_COLUMN_LAT, lat);
        values.put(DB_COLUMN_LON, lon);
        values.put(DB_COLUMN_MSG, message);
        values.put(DB_COLUMN_USER, user);
        long id = db.insert(DB_TABLE, null, values);
        db.close();

        return id;
    }

    public ImageMessage getById(long id) {
        String query = String.format(
                "SELECT * FROM %s WHERE %s=%s",
                DB_TABLE,
                DB_COLUMN_ID, id);
        List<ImageMessage> result = this.databaseSelectQuery(query);
        if (result.size() >= 1) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public List<ImageMessage> getSent(int sort, int limit, int offset) {
        if (limit == 0) {
            String query = String.format(
                    "SELECT * FROM %s WHERE %s=%s %s LIMIT -1 OFFSET %s",
                    DB_TABLE,
                    DB_COLUMN_TYPE, ImageMessage.TYPE_SENT,
                    this.queryPartOrderBy(sort),
                    offset);
            return this.databaseSelectQuery(query);
        } else {
            String query = String.format(
                    "SELECT * FROM %s WHERE %s=%s %s LIMIT %s OFFSET %s",
                    DB_TABLE,
                    DB_COLUMN_TYPE, ImageMessage.TYPE_SENT,
                    this.queryPartOrderBy(sort),
                    limit, offset);
            return this.databaseSelectQuery(query);
        }
    }

    public List<ImageMessage> getReceived(int sort, int limit, int offset) {
        if (limit == 0) {
            String query = String.format(
                    "SELECT * FROM %s WHERE %s=%s %s LIMIT -1 OFFSET %s",
                    DB_TABLE,
                    DB_COLUMN_TYPE, ImageMessage.TYPE_RECEIVED,
                    this.queryPartOrderBy(sort),
                    offset);
            return this.databaseSelectQuery(query);
        } else {
            String query = String.format(
                    "SELECT * FROM %s WHERE %s=%s %s LIMIT %s OFFSET %s",
                    DB_TABLE,
                    DB_COLUMN_TYPE, ImageMessage.TYPE_RECEIVED,
                    this.queryPartOrderBy(sort),
                    limit, offset);
            return this.databaseSelectQuery(query);
        }
    }

    public List<ImageMessage> getAll(int sort, int limit, int offset) {
        if (limit == 0) {
            String query = String.format(
                    "SELECT * FROM %s %s LIMIT -1 OFFSET %s",
                    DB_TABLE,
                    this.queryPartOrderBy(sort),
                    offset);
            return this.databaseSelectQuery(query);
        } else {
            String query = String.format(
                    "SELECT * FROM %s %s LIMIT %s OFFSET %s",
                    DB_TABLE,
                    this.queryPartOrderBy(sort),
                    limit, offset);
            return this.databaseSelectQuery(query);
        }
    }

    private List<ImageMessage> databaseSelectQuery(String query) {
        List<ImageMessage> result = new ArrayList<>();
        Log.d(TAG, "executing SQL query: " + query);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(DB_COLUMN_ID));
                long type = cursor.getLong(cursor.getColumnIndex(DB_COLUMN_TYPE));
                long date = cursor.getLong(cursor.getColumnIndex(DB_COLUMN_DATE));
                double lat = cursor.getDouble(cursor.getColumnIndex(DB_COLUMN_LAT));
                double lon = cursor.getDouble(cursor.getColumnIndex(DB_COLUMN_LON));
                String msg = cursor.getString(cursor.getColumnIndex(DB_COLUMN_MSG));
                String user = cursor.getString(cursor.getColumnIndex(DB_COLUMN_USER));

                ImageMessage imageMessage = new ImageMessage();
                imageMessage.setId(id);
                imageMessage.setType(type);
                imageMessage.setDate(new Date(date * 1000l)); // convert to milliseconds
                imageMessage.setLatitude(lat);
                imageMessage.setLongitude(lon);
                imageMessage.setMessage(msg);
                imageMessage.setUsername(user);

                result.add(imageMessage);
            } while (cursor.moveToNext());
        }

        db.close();
        return result;
    }

    private String queryPartOrderBy(int sort) {
        switch (sort) {
            case DB_SORT_DATE_ASC:
                return "ORDER BY " + DB_COLUMN_DATE + " ASC";
            case DB_SORT_DATE_DESC:
                return "ORDER BY " + DB_COLUMN_DATE + " DESC";
            case DB_SORT_LOCATION_ASC:
                return "ORDER BY " + DB_COLUMN_LAT + ", " + DB_COLUMN_LON + " ASC";
            case DB_SORT_LOCATION_DESC:
                return "ORDER BY " + DB_COLUMN_LAT + ", " + DB_COLUMN_LON + " DESC";
            default:
                return "";
        }
    }
}
