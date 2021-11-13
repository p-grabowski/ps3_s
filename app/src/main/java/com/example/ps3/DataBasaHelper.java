package com.example.ps3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "login.db";
    private static final int DB_VERSION = 4;
    private static final String TABLE_NAME = "users";
    private static final String ID_COL = "id";
    private static final String LOGIN_COL = "login";
    private static final String PASSWORD_COL = "password";
    private static final String IS_ADMIN_COL = "isAdmin";       //1 - admin, 0 - user
    private final SQLiteDatabase sqLiteDatabase;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LOGIN_COL + " TEXT,"
                + PASSWORD_COL + " TEXT,"
                + IS_ADMIN_COL + " INTEGER)";
        db.execSQL(query);
     }

    public void createUser(String login, String password, int isAdmin) throws NoSuchAlgorithmException {
        ContentValues values = new ContentValues();
        values.put(LOGIN_COL, login);
        values.put(PASSWORD_COL, hashUserPassword(password));
        values.put(IS_ADMIN_COL, isAdmin);

        sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    Map<String, Boolean> findUser(String login, String password) throws NoSuchAlgorithmException {
        Map<String, Boolean> result = new HashMap<>();
        String[] tableColumns = new String[]{
                LOGIN_COL,
                PASSWORD_COL,
                IS_ADMIN_COL
        };
        String whereClause = LOGIN_COL + "= ? AND " + PASSWORD_COL + " = ?";
        String[] whereArgs = new String[]{
                login,
                hashUserPassword(password)
        };
        Cursor query = sqLiteDatabase.query(TABLE_NAME, tableColumns, whereClause, whereArgs,
                null, null, null);
        if (query.getCount() == 1) {
            whereClause = LOGIN_COL + "= ? AND " + PASSWORD_COL + " = ? AND " + IS_ADMIN_COL + " = 1";
            Cursor adminQuery = sqLiteDatabase.query(TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, null);
            if (adminQuery.getCount() == 1) {
                result.put("Authorised", true);
                result.put("isAdmin", true);
            } else {
                result.put("Authorised", true);
                result.put("isAdmin", false);
            }
            adminQuery.close();
        } else {
            result.put("Authorised", false);
            result.put("isAdmin", false);
        }
        query.close();
        return result;
    }

    public boolean login(String username, String password)throws NoSuchAlgorithmException{
        password = hashUserPassword(password);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{PASSWORD_COL},LOGIN_COL + "=?",
                new String[]{username},null, null, null);
        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            if (password.equals(cursor.getString(0))) {
                return true;
            }
        }
        return false;
    }

//sprawdza po nazwie uzytkownika czy user jest adminem
    public boolean checkIsAdmin (String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(TABLE_NAME, new String[]{IS_ADMIN_COL},LOGIN_COL + "=?",
                new String[]{username},null, null, null);
        if (res != null && res.moveToFirst()&& res.getCount()>0) {
            return spr(res.getInt(0));
        }
        return false;
    }

    //zamienia 1 na true, 0 na false
    public boolean spr(int a){
        if(a==0)return false; else if(a==1) return true; else return false;
    }

    private String hashUserPassword(String plainText) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(
                plainText.getBytes(StandardCharsets.UTF_8));
        //return bytesToHex(encodedHash);
        return plainText;       //wyłączyłem szyfrowanie dla testu
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean checkUserIsExist(String login){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.query(TABLE_NAME, new String[]{ID_COL},LOGIN_COL + "=?",
                new String[]{login},null, null, null);
        if (res.getCount() > 0) return false;
        else return false;
    }
}
