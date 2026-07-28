package com.example.myapplication.data;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import com.example.myapplication.data.dao.BudgetDAO;
import com.example.myapplication.data.dao.BudgetDAO_Impl;
import com.example.myapplication.data.dao.CategoryDAO;
import com.example.myapplication.data.dao.CategoryDAO_Impl;
import com.example.myapplication.data.dao.NotificationDAO;
import com.example.myapplication.data.dao.NotificationDAO_Impl;
import com.example.myapplication.data.dao.TransactionDAO;
import com.example.myapplication.data.dao.TransactionDAO_Impl;
import com.example.myapplication.data.dao.UserDAO;
import com.example.myapplication.data.dao.UserDAO_Impl;
import com.example.myapplication.data.dao.UserSettingsDAO;
import com.example.myapplication.data.dao.UserSettingsDAO_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDAO _userDAO;

  private volatile CategoryDAO _categoryDAO;

  private volatile BudgetDAO _budgetDAO;

  private volatile TransactionDAO _transactionDAO;

  private volatile UserSettingsDAO _userSettingsDAO;

  private volatile NotificationDAO _notificationDAO;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(6, "c186556e69505b2f7834abf5e3b1f221", "da76cebe606f0f1f84b36d3152b6c053") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `users` (`userId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fullName` TEXT, `userName` TEXT, `password` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER, `name` TEXT, `icon` TEXT, `color` TEXT, `type` TEXT, `system` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `categoryId` INTEGER NOT NULL, `limitAmount` INTEGER NOT NULL, `month` INTEGER NOT NULL, `year` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_budgets_userId_categoryId_month_year` ON `budgets` (`userId`, `categoryId`, `month`, `year`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `categoryId` INTEGER NOT NULL, `amount` INTEGER NOT NULL, `note` TEXT, `transactionDate` INTEGER NOT NULL, `type` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `user_settings` (`userId` INTEGER NOT NULL, `isDarkMode` INTEGER NOT NULL, `isReminderEnabled` INTEGER NOT NULL, `reminderHour` INTEGER NOT NULL, `reminderMinute` INTEGER NOT NULL, `reminderNote` TEXT, PRIMARY KEY(`userId`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `message` TEXT, `createdAt` TEXT, `isRead` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c186556e69505b2f7834abf5e3b1f221')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `users`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `categories`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `budgets`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `transactions`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `user_settings`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `notifications`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(6);
        _columnsUsers.put("userId", new TableInfo.Column("userId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("fullName", new TableInfo.Column("fullName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("userName", new TableInfo.Column("userName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("password", new TableInfo.Column("password", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(connection, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenDelegate.ValidationResult(false, "users(com.example.myapplication.data.entity.User).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final Map<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(9);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("userId", new TableInfo.Column("userId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("icon", new TableInfo.Column("icon", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("color", new TableInfo.Column("color", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("system", new TableInfo.Column("system", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(connection, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenDelegate.ValidationResult(false, "categories(com.example.myapplication.data.entity.Category).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final Map<String, TableInfo.Column> _columnsBudgets = new HashMap<String, TableInfo.Column>(8);
        _columnsBudgets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("limitAmount", new TableInfo.Column("limitAmount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("month", new TableInfo.Column("month", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("year", new TableInfo.Column("year", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysBudgets = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesBudgets = new HashSet<TableInfo.Index>(1);
        _indicesBudgets.add(new TableInfo.Index("index_budgets_userId_categoryId_month_year", true, Arrays.asList("userId", "categoryId", "month", "year"), Arrays.asList("ASC", "ASC", "ASC", "ASC")));
        final TableInfo _infoBudgets = new TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets, _indicesBudgets);
        final TableInfo _existingBudgets = TableInfo.read(connection, "budgets");
        if (!_infoBudgets.equals(_existingBudgets)) {
          return new RoomOpenDelegate.ValidationResult(false, "budgets(com.example.myapplication.data.entity.Budget).\n"
                  + " Expected:\n" + _infoBudgets + "\n"
                  + " Found:\n" + _existingBudgets);
        }
        final Map<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(9);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("transactionDate", new TableInfo.Column("transactionDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(connection, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenDelegate.ValidationResult(false, "transactions(com.example.myapplication.data.entity.Transaction).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final Map<String, TableInfo.Column> _columnsUserSettings = new HashMap<String, TableInfo.Column>(6);
        _columnsUserSettings.put("userId", new TableInfo.Column("userId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("isDarkMode", new TableInfo.Column("isDarkMode", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("isReminderEnabled", new TableInfo.Column("isReminderEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("reminderHour", new TableInfo.Column("reminderHour", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("reminderMinute", new TableInfo.Column("reminderMinute", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("reminderNote", new TableInfo.Column("reminderNote", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysUserSettings = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesUserSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserSettings = new TableInfo("user_settings", _columnsUserSettings, _foreignKeysUserSettings, _indicesUserSettings);
        final TableInfo _existingUserSettings = TableInfo.read(connection, "user_settings");
        if (!_infoUserSettings.equals(_existingUserSettings)) {
          return new RoomOpenDelegate.ValidationResult(false, "user_settings(com.example.myapplication.data.entity.UserSettings).\n"
                  + " Expected:\n" + _infoUserSettings + "\n"
                  + " Found:\n" + _existingUserSettings);
        }
        final Map<String, TableInfo.Column> _columnsNotifications = new HashMap<String, TableInfo.Column>(5);
        _columnsNotifications.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("message", new TableInfo.Column("message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("createdAt", new TableInfo.Column("createdAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysNotifications = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesNotifications = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotifications = new TableInfo("notifications", _columnsNotifications, _foreignKeysNotifications, _indicesNotifications);
        final TableInfo _existingNotifications = TableInfo.read(connection, "notifications");
        if (!_infoNotifications.equals(_existingNotifications)) {
          return new RoomOpenDelegate.ValidationResult(false, "notifications(com.example.myapplication.data.entity.Notification).\n"
                  + " Expected:\n" + _infoNotifications + "\n"
                  + " Found:\n" + _existingNotifications);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users", "categories", "budgets", "transactions", "user_settings", "notifications");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "users", "categories", "budgets", "transactions", "user_settings", "notifications");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDAO.class, UserDAO_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDAO.class, CategoryDAO_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetDAO.class, BudgetDAO_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionDAO.class, TransactionDAO_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserSettingsDAO.class, UserSettingsDAO_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotificationDAO.class, NotificationDAO_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDAO userDAO() {
    if (_userDAO != null) {
      return _userDAO;
    } else {
      synchronized(this) {
        if(_userDAO == null) {
          _userDAO = new UserDAO_Impl(this);
        }
        return _userDAO;
      }
    }
  }

  @Override
  public CategoryDAO categoryDAO() {
    if (_categoryDAO != null) {
      return _categoryDAO;
    } else {
      synchronized(this) {
        if(_categoryDAO == null) {
          _categoryDAO = new CategoryDAO_Impl(this);
        }
        return _categoryDAO;
      }
    }
  }

  @Override
  public BudgetDAO budgetDAO() {
    if (_budgetDAO != null) {
      return _budgetDAO;
    } else {
      synchronized(this) {
        if(_budgetDAO == null) {
          _budgetDAO = new BudgetDAO_Impl(this);
        }
        return _budgetDAO;
      }
    }
  }

  @Override
  public TransactionDAO transactionDAO() {
    if (_transactionDAO != null) {
      return _transactionDAO;
    } else {
      synchronized(this) {
        if(_transactionDAO == null) {
          _transactionDAO = new TransactionDAO_Impl(this);
        }
        return _transactionDAO;
      }
    }
  }

  @Override
  public UserSettingsDAO userSettingsDAO() {
    if (_userSettingsDAO != null) {
      return _userSettingsDAO;
    } else {
      synchronized(this) {
        if(_userSettingsDAO == null) {
          _userSettingsDAO = new UserSettingsDAO_Impl(this);
        }
        return _userSettingsDAO;
      }
    }
  }

  @Override
  public NotificationDAO notificationDAO() {
    if (_notificationDAO != null) {
      return _notificationDAO;
    } else {
      synchronized(this) {
        if(_notificationDAO == null) {
          _notificationDAO = new NotificationDAO_Impl(this);
        }
        return _notificationDAO;
      }
    }
  }
}
