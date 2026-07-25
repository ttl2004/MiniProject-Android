package com.example.myapplication.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.myapplication.data.entity.Transaction;
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
public final class TransactionDAO_Impl implements TransactionDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Transaction> __insertAdapterOfTransaction;

  public TransactionDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfTransaction = new EntityInsertAdapter<Transaction>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `transactions` (`id`,`userId`,`categoryId`,`amount`,`note`,`transactionDate`,`type`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Transaction entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindLong(3, entity.getCategoryId());
        statement.bindLong(4, entity.getAmount());
        if (entity.getNote() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getNote());
        }
        statement.bindLong(6, entity.getTransactionDate());
        if (entity.getType() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getType());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
  }

  @Override
  public long insert(final Transaction transaction) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfTransaction.insertAndReturnId(_connection, transaction);
    });
  }

  @Override
  public LiveData<List<Transaction>> getTransactionsByUserIdAndRange(final int userId,
      final long startDate, final long endDate) {
    final String _sql = "SELECT * FROM transactions WHERE userId = ? AND transactionDate BETWEEN ? AND ? ORDER BY transactionDate DESC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"transactions"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, startDate);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, endDate);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfCategoryId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "categoryId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfNote = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "note");
        final int _columnIndexOfTransactionDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "transactionDate");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final List<Transaction> _result = new ArrayList<Transaction>();
        while (_stmt.step()) {
          final Transaction _item;
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final int _tmpCategoryId;
          _tmpCategoryId = (int) (_stmt.getLong(_columnIndexOfCategoryId));
          final int _tmpAmount;
          _tmpAmount = (int) (_stmt.getLong(_columnIndexOfAmount));
          final String _tmpNote;
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null;
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote);
          }
          final long _tmpTransactionDate;
          _tmpTransactionDate = _stmt.getLong(_columnIndexOfTransactionDate);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _item = new Transaction(_tmpUserId,_tmpCategoryId,_tmpAmount,_tmpNote,_tmpTransactionDate,_tmpType);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
