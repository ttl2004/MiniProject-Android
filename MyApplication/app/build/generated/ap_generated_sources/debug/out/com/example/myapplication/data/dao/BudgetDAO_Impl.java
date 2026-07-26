package com.example.myapplication.data.dao;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.data.entity.Budget;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BudgetDAO_Impl implements BudgetDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Budget> __insertionAdapterOfBudget;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBudget;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public BudgetDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBudget = new EntityInsertionAdapter<Budget>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `budgets` (`id`,`userId`,`categoryId`,`limitAmount`,`month`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Budget value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getUserId());
        stmt.bindLong(3, value.getCategoryId());
        stmt.bindLong(4, value.getLimitAmount());
        stmt.bindLong(5, value.getMonth());
        stmt.bindLong(6, value.getCreatedAt());
        stmt.bindLong(7, value.getUpdatedAt());
      }
    };
    this.__preparedStmtOfUpdateBudget = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE budgets SET categoryId = ?, limitAmount = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM budgets WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Budget budget) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfBudget.insertAndReturnId(budget);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int updateBudget(final int budgetId, final int categoryId, final int limitAmount,
      final long updatedAt) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBudget.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, categoryId);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, limitAmount);
    _argIndex = 3;
    _stmt.bindLong(_argIndex, updatedAt);
    _argIndex = 4;
    _stmt.bindLong(_argIndex, budgetId);
    __db.beginTransaction();
    try {
      final int _result = _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateBudget.release(_stmt);
    }
  }

  @Override
  public int deleteById(final int budgetId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, budgetId);
    __db.beginTransaction();
    try {
      final int _result = _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteById.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Budget>> getAll() {
    final String _sql = "SELECT * FROM budgets";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"budgets"}, false, new Callable<List<Budget>>() {
      @Override
      public List<Budget> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfLimitAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitAmount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<Budget> _result = new ArrayList<Budget>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Budget _item;
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            final int _tmpLimitAmount;
            _tmpLimitAmount = _cursor.getInt(_cursorIndexOfLimitAmount);
            final int _tmpMonth;
            _tmpMonth = _cursor.getInt(_cursorIndexOfMonth);
            _item = new Budget(_tmpUserId,_tmpCategoryId,_tmpLimitAmount,_tmpMonth);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item.setCreatedAt(_tmpCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item.setUpdatedAt(_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<BudgetItem>> getBudgetItems(final int userId, final int month,
      final String monthText, final String year) {
    final String _sql = "SELECT b.id AS budgetId, c.id AS categoryId, c.name AS categoryName, c.icon AS icon, c.color AS color, b.limitAmount AS limitAmount, IFNULL(SUM(t.amount), 0) AS spentAmount, 0 AS percent FROM budgets b JOIN categories c ON b.categoryId = c.id LEFT JOIN transactions t ON t.categoryId = b.categoryId AND t.type = 'EXPENSE' AND strftime('%m', t.transactionDate / 1000, 'unixepoch') = ? AND strftime('%Y', t.transactionDate / 1000, 'unixepoch') = ? WHERE b.userId = ? AND b.month = ? GROUP BY b.id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    if (monthText == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, monthText);
    }
    _argIndex = 2;
    if (year == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, year);
    }
    _argIndex = 3;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 4;
    _statement.bindLong(_argIndex, month);
    return __db.getInvalidationTracker().createLiveData(new String[]{"budgets","categories","transactions"}, false, new Callable<List<BudgetItem>>() {
      @Override
      public List<BudgetItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBudgetId = 0;
          final int _cursorIndexOfCategoryId = 1;
          final int _cursorIndexOfCategoryName = 2;
          final int _cursorIndexOfIcon = 3;
          final int _cursorIndexOfColor = 4;
          final int _cursorIndexOfLimitAmount = 5;
          final int _cursorIndexOfSpentAmount = 6;
          final int _cursorIndexOfPercent = 7;
          final List<BudgetItem> _result = new ArrayList<BudgetItem>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final BudgetItem _item;
            _item = new BudgetItem();
            _item.budgetId = _cursor.getInt(_cursorIndexOfBudgetId);
            _item.categoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            if (_cursor.isNull(_cursorIndexOfCategoryName)) {
              _item.categoryName = null;
            } else {
              _item.categoryName = _cursor.getString(_cursorIndexOfCategoryName);
            }
            if (_cursor.isNull(_cursorIndexOfIcon)) {
              _item.icon = null;
            } else {
              _item.icon = _cursor.getString(_cursorIndexOfIcon);
            }
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _item.color = null;
            } else {
              _item.color = _cursor.getString(_cursorIndexOfColor);
            }
            _item.limitAmount = _cursor.getLong(_cursorIndexOfLimitAmount);
            _item.spentAmount = _cursor.getLong(_cursorIndexOfSpentAmount);
            _item.percent = _cursor.getInt(_cursorIndexOfPercent);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
