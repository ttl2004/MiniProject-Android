package com.example.myapplication.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteConnectionUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.data.entity.Budget;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class BudgetDAO_Impl implements BudgetDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Budget> __insertAdapterOfBudget;

  public BudgetDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfBudget = new EntityInsertAdapter<Budget>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `budgets` (`id`,`userId`,`categoryId`,`limitAmount`,`month`,`year`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Budget entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindLong(3, entity.getCategoryId());
        statement.bindLong(4, entity.getLimitAmount());
        statement.bindLong(5, entity.getMonth());
        statement.bindLong(6, entity.getYear());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
  }

  @Override
  public long insert(final Budget budget) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfBudget.insertAndReturnId(_connection, budget);
    });
  }

  @Override
  public LiveData<List<Budget>> getAll() {
    final String _sql = "SELECT * FROM budgets";
    return __db.getInvalidationTracker().createLiveData(new String[] {"budgets"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfCategoryId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "categoryId");
        final int _columnIndexOfLimitAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "limitAmount");
        final int _columnIndexOfMonth = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "month");
        final int _columnIndexOfYear = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "year");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final List<Budget> _result = new ArrayList<Budget>();
        while (_stmt.step()) {
          final Budget _item;
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final int _tmpCategoryId;
          _tmpCategoryId = (int) (_stmt.getLong(_columnIndexOfCategoryId));
          final int _tmpLimitAmount;
          _tmpLimitAmount = (int) (_stmt.getLong(_columnIndexOfLimitAmount));
          final int _tmpMonth;
          _tmpMonth = (int) (_stmt.getLong(_columnIndexOfMonth));
          final int _tmpYear;
          _tmpYear = (int) (_stmt.getLong(_columnIndexOfYear));
          _item = new Budget(_tmpUserId,_tmpCategoryId,_tmpLimitAmount,_tmpMonth,_tmpYear);
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          _item.setId(_tmpId);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item.setCreatedAt(_tmpCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          _item.setUpdatedAt(_tmpUpdatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<BudgetItem>> getBudgetItems(final int userId, final int month,
      final int yearInt, final String monthText, final String year) {
    final String _sql = "SELECT b.id AS budgetId, c.id AS categoryId, c.name AS categoryName, c.icon AS icon, c.color AS color, b.limitAmount AS limitAmount, IFNULL(SUM(t.amount), 0) AS spentAmount, 0 AS percent FROM budgets b JOIN categories c ON b.categoryId = c.id LEFT JOIN transactions t ON t.categoryId = b.categoryId AND t.type = 'EXPENSE' AND strftime('%m', t.transactionDate / 1000, 'unixepoch') = ? AND strftime('%Y', t.transactionDate / 1000, 'unixepoch') = ? WHERE b.userId = ? AND b.month = ? AND b.year = ? GROUP BY b.id";
    return __db.getInvalidationTracker().createLiveData(new String[] {"budgets", "categories",
        "transactions"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (monthText == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, monthText);
        }
        _argIndex = 2;
        if (year == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, year);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, userId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, month);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, yearInt);
        final int _columnIndexOfBudgetId = 0;
        final int _columnIndexOfCategoryId = 1;
        final int _columnIndexOfCategoryName = 2;
        final int _columnIndexOfIcon = 3;
        final int _columnIndexOfColor = 4;
        final int _columnIndexOfLimitAmount = 5;
        final int _columnIndexOfSpentAmount = 6;
        final int _columnIndexOfPercent = 7;
        final List<BudgetItem> _result = new ArrayList<BudgetItem>();
        while (_stmt.step()) {
          final BudgetItem _item;
          _item = new BudgetItem();
          _item.budgetId = (int) (_stmt.getLong(_columnIndexOfBudgetId));
          _item.categoryId = (int) (_stmt.getLong(_columnIndexOfCategoryId));
          if (_stmt.isNull(_columnIndexOfCategoryName)) {
            _item.categoryName = null;
          } else {
            _item.categoryName = _stmt.getText(_columnIndexOfCategoryName);
          }
          if (_stmt.isNull(_columnIndexOfIcon)) {
            _item.icon = null;
          } else {
            _item.icon = _stmt.getText(_columnIndexOfIcon);
          }
          if (_stmt.isNull(_columnIndexOfColor)) {
            _item.color = null;
          } else {
            _item.color = _stmt.getText(_columnIndexOfColor);
          }
          _item.limitAmount = _stmt.getLong(_columnIndexOfLimitAmount);
          _item.spentAmount = _stmt.getLong(_columnIndexOfSpentAmount);
          _item.percent = (int) (_stmt.getLong(_columnIndexOfPercent));
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public int updateBudget(final int budgetId, final int categoryId, final int limitAmount,
      final long updatedAt) {
    final String _sql = "UPDATE budgets SET categoryId = ?, limitAmount = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, categoryId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, limitAmount);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, budgetId);
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public int deleteById(final int budgetId) {
    final String _sql = "DELETE FROM budgets WHERE id = ?";
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, budgetId);
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
