package com.mindmac.eagleeye.hookclass;

import java.util.ArrayList;
import java.util.List;

import com.mindmac.eagleeye.service.Launcher;
import android.annotation.SuppressLint;
import android.os.Binder;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class SQLiteDatabase extends MethodHook {
    private Methods mMethod;

    private static final String mClassName = "android.database.sqlite.SQLiteDatabase";

    private SQLiteDatabase(SQLiteDatabase.Methods method) {
        super( mClassName, method.name());
        mMethod = method;
    }
//TODO запилить для класса SQLiteStament
    // @formatter:off

    // public SQLiteStatement compileStatement (String sql)
    // public int delete (String table, String whereClause, String[] whereArgs)
    // public static boolean deleteDatabase (File file)
    // public void execSQL (String sql)
    // public void execSQL (String sql, Object[] bindArgs)
    // public long insert (String table, String nullColumnHack, ContentValues values)
    // public long insertOrThrow (String table, String nullColumnHack, ContentValues values)
    // public long insertWithOnConflict (String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm)
    // public static SQLiteDatabase openDatabase (String path, SQLiteDatabase.CursorFactory factory, int flags)
    // public static SQLiteDatabase openDatabase (File path, SQLiteDatabase.OpenParams openParams)
    // public static SQLiteDatabase openDatabase (String path, SQLiteDatabase.CursorFactory factory, int flags, DatabaseErrorHandler errorHandler)
    // public static SQLiteDatabase openOrCreateDatabase (File file, SQLiteDatabase.CursorFactory factory)
    // public static SQLiteDatabase openOrCreateDatabase (String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler)
    // public static SQLiteDatabase openOrCreateDatabase (String path, SQLiteDatabase.CursorFactory factory)
    // public Cursor query (boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
    // public Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
    // public Cursor query (boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal)
    // public Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
    // public Cursor queryWithFactory (SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal)
    // public Cursor queryWithFactory (SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
    // public Cursor rawQuery (String sql, String[] selectionArgs, CancellationSignal cancellationSignal)
    // public Cursor rawQuery (String sql, String[] selectionArgs)
    // public Cursor rawQueryWithFactory (SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable, CancellationSignal cancellationSignal)
    // public Cursor rawQueryWithFactory (SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable)
    // public int update (String table, ContentValues values, String whereClause, String[] whereArgs)
    // public int updateWithOnConflict (String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm)
    // public void validateSql (String sql, CancellationSignal cancellationSignal)

    // frameworks/base/core/java/android/app/Activity.java

    // @formatter:on

    private enum Methods {
        compileStatement, delete, deleteDatabase, execSQL, insert, insertOrThrow, insertWithOnConflict, openDatabase,
        openOrCreateDatabase, query, queryWithFactory, rawQuery, rawQueryWithFactory, update, updateWithOnConflict, validateSql
    };

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<MethodHook>();
        methodHookList.add(new SQLiteDatabase(Methods.compileStatement));
        methodHookList.add(new SQLiteDatabase(Methods.delete));
        methodHookList.add(new SQLiteDatabase(Methods.deleteDatabase));
        methodHookList.add(new SQLiteDatabase(Methods.execSQL));
        methodHookList.add(new SQLiteDatabase(Methods.insert));
        methodHookList.add(new SQLiteDatabase(Methods.insertOrThrow));
        methodHookList.add(new SQLiteDatabase(Methods.insertWithOnConflict));
        methodHookList.add(new SQLiteDatabase(Methods.openDatabase));
        methodHookList.add(new SQLiteDatabase(Methods.openOrCreateDatabase));
        methodHookList.add(new SQLiteDatabase(Methods.query));
        methodHookList.add(new SQLiteDatabase(Methods.queryWithFactory));
        methodHookList.add(new SQLiteDatabase(Methods.rawQuery));
        methodHookList.add(new SQLiteDatabase(Methods.rawQueryWithFactory));
        methodHookList.add(new SQLiteDatabase(Methods.update));
        methodHookList.add(new SQLiteDatabase(Methods.updateWithOnConflict));
        methodHookList.add(new SQLiteDatabase(Methods.validateSql));
        return methodHookList;
    }

    @Override
    public void after(MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = null;

        if(mMethod == Methods.compileStatement){
            argNames = "sql";
        }else if(mMethod == Methods.delete){
            argNames = "table|whereClause|whereArgs";
        }else if(mMethod == Methods.deleteDatabase){
            argNames = "file";
        }else if(mMethod == Methods.execSQL){
            if(param.args.length == 1)
                argNames = "sql";
            else
                argNames = "sql|bindArgs";
        }else if(mMethod == Methods.insert){
            argNames = "table|nullColumnHack|values";
        }else if(mMethod == Methods.insertOrThrow){
            argNames = "table|nullColumnHack|values";
        }else if(mMethod == Methods.insertWithOnConflict){
            argNames = "table|nullColumnHack|initialValues|conflictAlgorithm";
        }else if(mMethod == Methods.openDatabase){
            if(param.args.length == 2)
                argNames = "path|openParams";
            else if (param.args.length == 3)
                argNames = "path|factory|flags";
            else
                argNames = "path|factory|flags|errorHandler";
        }else if(mMethod == Methods.openOrCreateDatabase){
            if(param.args.length == 2) {
                if (param.args[0] instanceof String)
                    argNames = "path|factory";
                else
                    argNames = "file|factory";
            }
            else
                argNames = "path|factory|errorHandler";
        }else if(mMethod == Methods.query){
            if(param.args.length == 7)
                argNames = "table|columns|selection|selectionArgs|groupBy|having|orderBy";
            else if (param.args.length == 8)
                argNames = "table|columns|selection|selectionArgs|groupBy|having|orderBy|limit";
            else if (param.args.length == 9)
                argNames = "distinct|table|columns|selection|selectionArgs|groupBy|having|orderBy|limit";
            else
                argNames = "distinct|table|columns|selection|selectionArgs|groupBy|having|orderBy|limit|cancellationSignal";
        }else if(mMethod == Methods.queryWithFactory){
            if(param.args.length == 10)
                argNames = "cursorFactory|distinct|table|columns|selection|selectionArgs|groupBy|having|orderBy|limit";
            else
                argNames = "cursorFactory|distinct|table|columns|selection|selectionArgs|groupBy|having|orderBy|limit|cancellationSignal";
        }else if(mMethod == Methods.rawQuery){
            if(param.args.length == 2)
                argNames = "sql|selectionArgs|cancellationSignal";
            else
                argNames = "sql|selectionArgs|cancellationSignal";
        }else if(mMethod == Methods.rawQueryWithFactory){
            if(param.args.length == 4)
                argNames = "cursorFactory|sql|selectionArgs|editTable";
            else
                argNames = "cursorFactory|sql|selectionArgs|editTable|cancellationSignal";
        }else if(mMethod == Methods.update){
            argNames = "table|values|whereClause|whereArgs";
        }else if(mMethod == Methods.updateWithOnConflict){
            argNames = "table|values|whereClause|whereArgs|conflictAlgorithm";
        }else if(mMethod == Methods.validateSql){
            argNames = "sql|cancellationSignal";
        }

        log(uid, param, argNames);
    }
}
