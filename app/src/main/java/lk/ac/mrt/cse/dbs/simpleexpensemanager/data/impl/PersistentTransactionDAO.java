package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.DATE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.EXPENSE_TYPE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.TRANSACTION_TABLE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final DB_Manager manager;

    private SQLiteDatabase database;

    public PersistentTransactionDAO(Context context) {

        manager = new DB_Manager(context);
    }

    @Override
    public void logTransaction(Date dateT, String accNo, ExpenseType type, double amnt) {
        database = manager.getWritableDatabase();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, dateFormat.format(dateT));
        contentValues.put(ACCOUNT_NO, accNo);
        contentValues.put(EXPENSE_TYPE, String.valueOf(type));
        contentValues.put(AMOUNT, amnt);
        database.insert(
                TRANSACTION_TABLE,
                null,
                contentValues);
        database.close();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions = new ArrayList<>();

        database = manager.getReadableDatabase();

        String[] columns = {DATE, ACCOUNT_NO, EXPENSE_TYPE, AMOUNT};
        Cursor cursor = database.query(TRANSACTION_TABLE,
                columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String tempDate0 = cursor.getString(cursor.getColumnIndex(DATE));

            @SuppressLint("SimpleDateFormat") Date tempDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(tempDate0);

            String accountNo = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));

            String type = cursor.getString(cursor.getColumnIndex(EXPENSE_TYPE));
            ExpenseType Type = ExpenseType.valueOf(type);

            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));

            transactions.add(new Transaction(tempDate1, accountNo, Type, amount));
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {

        List<Transaction> transactions = new ArrayList<>();

        database = manager.getReadableDatabase();
        String[] columns = {DATE, ACCOUNT_NO, EXPENSE_TYPE, AMOUNT};
        Cursor cursor = database.query(TRANSACTION_TABLE,
                columns, null, null, null, null, null);


        while (cursor.moveToNext()) {
            String tempDate0 = cursor.getString(cursor.getColumnIndex(DATE));

            @SuppressLint("SimpleDateFormat") Date tempDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(tempDate0);

            String accontNo = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));

            String type = cursor.getString(cursor.getColumnIndex(EXPENSE_TYPE));
            ExpenseType Type = ExpenseType.valueOf(type);

            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));

            Transaction transaction = new Transaction(tempDate1, accontNo, Type, amount);
            transactions.add(transaction);
        }
        cursor.close();
        if (cursor.getCount() <= limit) {
            return transactions;}

        return transactions.subList(cursor.getCount() - limit, cursor.getCount());
    }
}
