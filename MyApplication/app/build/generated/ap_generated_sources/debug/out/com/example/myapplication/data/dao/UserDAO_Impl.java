package com.example.myapplication.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.myapplication.data.entity.User;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class UserDAO_Impl implements UserDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<User> __insertAdapterOfUser;

  private final EntityDeleteOrUpdateAdapter<User> __updateAdapterOfUser;

  public UserDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfUser = new EntityInsertAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `users` (`userId`,`fullName`,`userName`,`password`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        statement.bindLong(1, entity.getUserId());
        if (entity.getFullName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getFullName());
        }
        if (entity.getUserName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getUserName());
        }
        if (entity.getPassword() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getPassword());
        }
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindLong(6, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfUser = new EntityDeleteOrUpdateAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `userId` = ?,`fullName` = ?,`userName` = ?,`password` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `userId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        statement.bindLong(1, entity.getUserId());
        if (entity.getFullName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getFullName());
        }
        if (entity.getUserName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getUserName());
        }
        if (entity.getPassword() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getPassword());
        }
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindLong(6, entity.getUpdatedAt());
        statement.bindLong(7, entity.getUserId());
      }
    };
  }

  @Override
  public long insert(final User user) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfUser.insertAndReturnId(_connection, user);
    });
  }

  @Override
  public int update(final User user) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      int _result = 0;
      _result += __updateAdapterOfUser.handle(_connection, user);
      return _result;
    });
  }

  @Override
  public User login(final String userName, final String password) {
    final String _sql = "SELECT * FROM users WHERE userName = ? AND password = ? LIMIT 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userName);
        }
        _argIndex = 2;
        if (password == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, password);
        }
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfFullName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fullName");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final User _result;
        if (_stmt.step()) {
          final String _tmpFullName;
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null;
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName);
          }
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          final String _tmpPassword;
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _tmpPassword = null;
          } else {
            _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          }
          _result = new User(_tmpFullName,_tmpUserName,_tmpPassword);
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          _result.setUserId(_tmpUserId);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _result.setCreatedAt(_tmpCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          _result.setUpdatedAt(_tmpUpdatedAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public User getUserByUsername(final String userName) {
    final String _sql = "SELECT * FROM users WHERE userName = ? LIMIT 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userName);
        }
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfFullName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fullName");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final User _result;
        if (_stmt.step()) {
          final String _tmpFullName;
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null;
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName);
          }
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          final String _tmpPassword;
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _tmpPassword = null;
          } else {
            _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          }
          _result = new User(_tmpFullName,_tmpUserName,_tmpPassword);
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          _result.setUserId(_tmpUserId);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _result.setCreatedAt(_tmpCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          _result.setUpdatedAt(_tmpUpdatedAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public boolean isUsernameExists(final String userName) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM users WHERE userName = ? LIMIT 1)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userName);
        }
        final boolean _result;
        if (_stmt.step()) {
          final int _tmp;
          _tmp = (int) (_stmt.getLong(0));
          _result = _tmp != 0;
        } else {
          _result = false;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public User findByUsernameAndFullName(final String username, final String fullName) {
    final String _sql = "SELECT * FROM users WHERE username = ? AND fullName = ? LIMIT 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (username == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, username);
        }
        _argIndex = 2;
        if (fullName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, fullName);
        }
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfFullName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fullName");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final User _result;
        if (_stmt.step()) {
          final String _tmpFullName;
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null;
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName);
          }
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          final String _tmpPassword;
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _tmpPassword = null;
          } else {
            _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          }
          _result = new User(_tmpFullName,_tmpUserName,_tmpPassword);
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          _result.setUserId(_tmpUserId);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _result.setCreatedAt(_tmpCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          _result.setUpdatedAt(_tmpUpdatedAt);
        } else {
          _result = null;
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
