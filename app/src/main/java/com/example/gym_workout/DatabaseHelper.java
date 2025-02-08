    package com.example.gym_workout;


    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    public class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "gym.db";
        private static final int DATABASE_VERSION = 2;

        public static final String TABLE_USERS = "users";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";

        private static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USERNAME + " TEXT NOT NULL, " +
                        COLUMN_PASSWORD + " TEXT NOT NULL);";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }

        public boolean registerUser(String username, String password) {
            SQLiteDatabase db = this.getWritableDatabase();
            String insertQuery = "INSERT INTO " + TABLE_USERS +
                    " (" + COLUMN_USERNAME + ", " +
                    COLUMN_PASSWORD + ") VALUES ('" +
                    username + "', '" + password + "');";
            try {
                db.execSQL(insertQuery);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public void addTestUser(String username, String password) {
            SQLiteDatabase db = this.getWritableDatabase();
            String insertQuery = "INSERT INTO " + TABLE_USERS +
                    " (" + COLUMN_USERNAME + ", " +
                    COLUMN_PASSWORD + ") VALUES ('" +
                    username + "', '" + password + "');";
            db.execSQL(insertQuery);
        }


        public boolean checkUser(String username, String password) {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_USERS +
                    " WHERE " + COLUMN_USERNAME + " = '" + username +
                    "' AND " + COLUMN_PASSWORD + " = '" + password + "';";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        }
    }
