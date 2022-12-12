package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.ACCOUNT_HOLDER_NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.ACCOUNT_TABLE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DB_Manager.BANK_NAME;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentAccountDAO implements AccountDAO {
    private final DB_Manager manager;
    private SQLiteDatabase database;

    public PersistentAccountDAO(Context context) {

        manager = new DB_Manager(context);
    }

    @Override
    public List<String> getAccountNumbersList() {

        database = manager.getReadableDatabase();
        String[] columns = {ACCOUNT_NO};

        Cursor cursor = database.query(ACCOUNT_TABLE, columns, null, null, null, null, null);

        List<String> accountNumberList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int n =cursor.getColumnIndexOrThrow(ACCOUNT_NO);

            accountNumberList.add(cursor.getString(n));
        }
        cursor.close();
        return accountNumberList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();

        database = manager.getReadableDatabase();

        String[] columns = {
                ACCOUNT_NO,
                BANK_NAME,
                ACCOUNT_HOLDER_NAME,
                BALANCE};
        Cursor cursor = database.query(ACCOUNT_TABLE, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String accontNO =cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String bankName =cursor.getString(cursor.getColumnIndex(BANK_NAME));
            String holderName =cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDER_NAME));
            double balance =cursor.getDouble(cursor.getColumnIndex(BALANCE));

            Account account = new Account( accontNO,bankName  , holderName, balance);
            accounts.add(account);
        }
        cursor.close();

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account;
        database = manager.getReadableDatabase();
        String[] selection  = {ACCOUNT_NO, BANK_NAME, ACCOUNT_HOLDER_NAME, BALANCE};

        String condition = ACCOUNT_NO + " = ?";

        String[] whereArgs = {accountNo};
        // generating queary
        Cursor cursor = database.query(ACCOUNT_TABLE, selection, condition, whereArgs, null, null, null);
        if (cursor == null) {

            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {

            cursor.moveToFirst();
            String bank_name= cursor.getString(cursor.getColumnIndex(BANK_NAME));
            String holder_name= cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDER_NAME));
            double balance =cursor.getDouble(cursor.getColumnIndex(BALANCE));
            account = new Account(accountNo, bank_name, holder_name,balance );
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {

        database = manager.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(ACCOUNT_NO, account.getAccountNo());
        contentValues.put(BANK_NAME, account.getBankName());
        contentValues.put(ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        contentValues.put(BALANCE, account.getBalance());

        database.insert(ACCOUNT_TABLE, null, contentValues);
        database.close();
    }

    @Override
    public void removeAccount(String accountNo) {
        database = manager.getWritableDatabase();
        String whereClause = ACCOUNT_NO + "=?";
        String[] whereArgs = {accountNo};

        database.delete(ACCOUNT_TABLE,whereClause ,  whereArgs);
        database.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        database = manager.getWritableDatabase();
        double balance;
        String[] columns = {BALANCE};

        String selection = ACCOUNT_NO + " = ?";
        String[] conditionArgs = {accountNo};
        Cursor cursor = database.query(ACCOUNT_TABLE,
                columns,
                selection,
                conditionArgs, null, null, null);


        if (cursor.moveToFirst()) {

            balance = cursor.getDouble(0);
        } else {

            String msg = "Account " + accountNo + " is invalid.";

            throw new InvalidAccountException(msg);
        }

        ContentValues contentValues = new ContentValues();

        if (expenseType == ExpenseType.EXPENSE){
            double newBalance = balance - amount;
            contentValues.put(BALANCE , newBalance);
        }
        else {
            double newBalance = balance + amount;
            contentValues.put(BALANCE , newBalance);

        }
        database.update(ACCOUNT_TABLE,
                contentValues,
                ACCOUNT_NO + " = ?",
                new String[]{accountNo});

        cursor.close();
        database.close();

    }
}
