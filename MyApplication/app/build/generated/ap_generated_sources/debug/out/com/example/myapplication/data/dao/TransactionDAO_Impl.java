package com.example.myapplication.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.myapplication.data.entity.Transaction;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
