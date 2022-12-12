package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Manager extends SQLiteOpenHelper {


    private static final String DATABASE_NAME ="20021T";

    //account table
    public static final String ACCOUNT_TABLE="Account";
    //  transaction table
    public static final String TRANSACTION_TABLE ="Transactions";
    // cloumns of the account table
    public static final String ACCOUNT_NO ="AccountNo";
    public static final String BANK_NAME ="BankName";
    public static final String ACCOUNT_HOLDER_NAME ="AccountHolderName";
    public static final String BALANCE ="Balance";
    // columns of the transaction table
    public static final String ID ="Id";
    public static final String DATE ="Date";
    public static final String EXPENSE_TYPE ="ExpenseType";
    public static final String AMOUNT ="Amount";

    public DB_Manager(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create account table
        sqLiteDatabase.execSQL("create table " + ACCOUNT_TABLE + "(" +
                ACCOUNT_NO + " text primary key," +
                BANK_NAME + " text not null," +

                ACCOUNT_HOLDER_NAME + " text not null," +

                BALANCE + " real not null)");
        // create transaction table
        sqLiteDatabase.execSQL("create table " + TRANSACTION_TABLE + "(" +
                ID + " integer primary key autoincrement, " +
                DATE + " text not null," +
                EXPENSE_TYPE + " text not null," +
                AMOUNT + " real not null, " +
                ACCOUNT_NO + " text," +

                "foreign key (" + ACCOUNT_NO +") references " + ACCOUNT_TABLE + "(" + ACCOUNT_NO + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old , int newV) {

        sqLiteDatabase.execSQL("drop table if exists " + ACCOUNT_TABLE);

        sqLiteDatabase.execSQL("drop table if exists " + TRANSACTION_TABLE);

        onCreate(sqLiteDatabase);
    }
}
