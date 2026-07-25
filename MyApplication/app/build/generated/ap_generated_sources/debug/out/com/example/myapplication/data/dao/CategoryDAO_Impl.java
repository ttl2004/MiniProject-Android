package com.example.myapplication.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.myapplication.data.entity.Category;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class CategoryDAO_Impl implements CategoryDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Category> __insertAdapterOfCategory;

  public CategoryDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfCategory = new EntityInsertAdapter<Category>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `categories` (`id`,`userId`,`name`,`icon`,`color`,`system`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Category entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        if (entity.getName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getName());
        }
        if (entity.getIcon() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getIcon());
        }
        if (entity.getColor() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getColor());
        }
        final int _tmp = entity.isSystem() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
  }

  @Override
  public long insert(final Category category) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfCategory.insertAndReturnId(_connection, category);
    });
  }

  @Override
  public void insertALL(final List<Category> categoryList) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfCategory.insert(_connection, categoryList);
      return null;
    });
  }

  @Override
  public LiveData<List<Category>> getALL() {
    final String _sql = "SELECT * FROM categories";
    return __db.getInvalidationTracker().createLiveData(new String[] {"categories"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfIcon = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "icon");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfSystem = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "system");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final List<Category> _result = new ArrayList<Category>();
        while (_stmt.step()) {
          final Category _item;
          _item = new Category();
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          _item.setId(_tmpId);
          final Integer _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          }
          _item.setUserId(_tmpUserId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item.setName(_tmpName);
          final String _tmpIcon;
          if (_stmt.isNull(_columnIndexOfIcon)) {
            _tmpIcon = null;
          } else {
            _tmpIcon = _stmt.getText(_columnIndexOfIcon);
          }
          _item.setIcon(_tmpIcon);
          final String _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor);
          }
          _item.setColor(_tmpColor);
          final boolean _tmpSystem;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfSystem));
          _tmpSystem = _tmp != 0;
          _item.setSystem(_tmpSystem);
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
