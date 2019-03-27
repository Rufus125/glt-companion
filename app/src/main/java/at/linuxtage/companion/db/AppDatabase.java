package at.linuxtage.companion.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import at.linuxtage.companion.db.converters.GlobalTypeConverters;
import at.linuxtage.companion.db.entities.Bookmark;
import at.linuxtage.companion.db.entities.EventEntity;
import at.linuxtage.companion.db.entities.EventTitles;
import at.linuxtage.companion.db.entities.EventToPerson;
import at.linuxtage.companion.model.Day;
import at.linuxtage.companion.model.Link;
import at.linuxtage.companion.model.Person;
import at.linuxtage.companion.model.Track;
import at.linuxtage.companion.utils.DateUtils;

@Database(entities = {EventEntity.class, EventTitles.class, Person.class, EventToPerson.class, Link.class, Track.class, Day.class, Bookmark.class}, version = 3, exportSchema = false)
@TypeConverters({GlobalTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

	private static final String DB_PREFS_FILE = "database";
	private static volatile AppDatabase INSTANCE;
	private int year = -1;

	static final Migration MIGRATION_1_2 = new Migration(1, 2) {
		@Override
		public void migrate(SupportSQLiteDatabase database) {
			// Events: make primary key and track_id not null
			database.execSQL("CREATE TABLE tmp_"
					+ EventEntity.TABLE_NAME
					+ " (id INTEGER PRIMARY KEY NOT NULL, day_index INTEGER NOT NULL, start_time INTEGER, end_time INTEGER, room_name TEXT, slug TEXT, url TEXT, track_id INTEGER NOT NULL, abstract TEXT, description TEXT);");
			database.execSQL("INSERT INTO tmp_" + EventEntity.TABLE_NAME + " SELECT * FROM " + EventEntity.TABLE_NAME);
			database.execSQL("DROP TABLE " + EventEntity.TABLE_NAME);
			database.execSQL("ALTER TABLE tmp_" + EventEntity.TABLE_NAME + " RENAME TO " + EventEntity.TABLE_NAME);
			database.execSQL("CREATE INDEX event_day_index_idx ON " + EventEntity.TABLE_NAME + " (day_index)");
			database.execSQL("CREATE INDEX event_start_time_idx ON " + EventEntity.TABLE_NAME + " (start_time)");
			database.execSQL("CREATE INDEX event_end_time_idx ON " + EventEntity.TABLE_NAME + " (end_time)");
			database.execSQL("CREATE INDEX event_track_id_idx ON " + EventEntity.TABLE_NAME + " (track_id)");

			// Links: add explicit primary key
			database.execSQL("CREATE TABLE tmp_" + Link.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, event_id INTEGER NOT NULL, url TEXT NOT NULL, description TEXT);");
			database.execSQL("INSERT INTO tmp_" + Link.TABLE_NAME + " SELECT `rowid` AS id, event_id, url, description FROM " + Link.TABLE_NAME);
			database.execSQL("DROP TABLE " + Link.TABLE_NAME);
			database.execSQL("ALTER TABLE tmp_" + Link.TABLE_NAME + " RENAME TO " + Link.TABLE_NAME);
			database.execSQL("CREATE INDEX link_event_id_idx ON " + Link.TABLE_NAME + " (event_id)");

			// Tracks: make primary key not null
			database.execSQL("CREATE TABLE tmp_" + Track.TABLE_NAME + " (id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, type TEXT NOT NULL);");
			database.execSQL("INSERT INTO tmp_" + Track.TABLE_NAME + " SELECT * FROM " + Track.TABLE_NAME);
			database.execSQL("DROP TABLE " + Track.TABLE_NAME);
			database.execSQL("ALTER TABLE tmp_" + Track.TABLE_NAME + " RENAME TO " + Track.TABLE_NAME);
			database.execSQL("CREATE UNIQUE INDEX track_main_idx ON " + Track.TABLE_NAME + " (name, type)");

			// Days: make primary key not null and rename _index to index
			database.execSQL("CREATE TABLE tmp_" + Day.TABLE_NAME + " (`index` INTEGER PRIMARY KEY NOT NULL, date INTEGER NOT NULL);");
			database.execSQL("INSERT INTO tmp_" + Day.TABLE_NAME + " SELECT _index as `index`, date FROM " + Day.TABLE_NAME);
			database.execSQL("DROP TABLE " + Day.TABLE_NAME);
			database.execSQL("ALTER TABLE tmp_" + Day.TABLE_NAME + " RENAME TO " + Day.TABLE_NAME);

			// Bookmarks: make primary key not null
			database.execSQL("CREATE TABLE tmp_" + Bookmark.TABLE_NAME + " (event_id INTEGER PRIMARY KEY NOT NULL);");
			database.execSQL("INSERT INTO tmp_" + Bookmark.TABLE_NAME + " SELECT * FROM " + Bookmark.TABLE_NAME);
			database.execSQL("DROP TABLE " + Bookmark.TABLE_NAME);
			database.execSQL("ALTER TABLE tmp_" + Bookmark.TABLE_NAME + " RENAME TO " + Bookmark.TABLE_NAME);
		}
	};


	static final Migration MIGRATION_2_3 = new Migration(2, 3) {
		@Override
		public void migrate(SupportSQLiteDatabase database) {
			// Events: add URL field
			database.execSQL("DROP TABLE " + EventEntity.TABLE_NAME);
			database.execSQL("CREATE TABLE "
					+ EventEntity.TABLE_NAME
					+ " (id INTEGER PRIMARY KEY NOT NULL, day_index INTEGER NOT NULL, start_time INTEGER, end_time INTEGER, room_name TEXT, slug TEXT, url TEXT, track_id INTEGER NOT NULL, abstract TEXT, description TEXT);");
			database.execSQL("CREATE INDEX event_day_index_idx ON " + EventEntity.TABLE_NAME + " (day_index)");
			database.execSQL("CREATE INDEX event_start_time_idx ON " + EventEntity.TABLE_NAME + " (start_time)");
			database.execSQL("CREATE INDEX event_end_time_idx ON " + EventEntity.TABLE_NAME + " (end_time)");
			database.execSQL("CREATE INDEX event_track_id_idx ON " + EventEntity.TABLE_NAME + " (track_id)");
		}
	};

	private SharedPreferences sharedPreferences;

	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

	public static AppDatabase getInstance(Context context) {
		AppDatabase res = INSTANCE;
		if (res == null) {
			synchronized (AppDatabase.class) {
				res = INSTANCE;
				if (res == null) {
					res = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "glt.sqlite")
							.addMigrations(MIGRATION_1_2)
							.addMigrations(MIGRATION_2_3)
							.setJournalMode(JournalMode.TRUNCATE)
							.build();
					res.sharedPreferences = context.getApplicationContext().getSharedPreferences(DB_PREFS_FILE, Context.MODE_PRIVATE);
					INSTANCE = res;
				}
			}
		}
		return res;
	}

	public int getYear() {
		// Try to get the cached value first
		if (year != -1) {
			return year;
		}

		Calendar cal = Calendar.getInstance(DateUtils.getAustriaTimeZone(), Locale.US);
/*
		// Compute from cached days if available
		List<Day> days = daysLiveData.getValue();
		if (days != null) {
			if (days.size() > 0) {
				cal.setTime(days.get(0).getDate());
			}
		} else {
			// Perform a quick DB query to retrieve the time of the first day
			long date = DatabaseUtils.longForQuery(helper.getReadableDatabase(),
					"SELECT date FROM " + DatabaseHelper.DAYS_TABLE_NAME + " ORDER BY _index ASC LIMIT 1", null);
			cal.setTimeInMillis(date);
		}
*/
		// If the calendar has not been set at this point, it will simply return the current year
		year = cal.get(Calendar.YEAR);
		return year;
	}

	public abstract ScheduleDao getScheduleDao();

	public abstract BookmarksDao getBookmarksDao();
}
