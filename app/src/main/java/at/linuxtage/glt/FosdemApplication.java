package at.linuxtage.glt;

import android.app.Application;
import android.preference.PreferenceManager;
import at.linuxtage.glt.alarms.FosdemAlarmManager;
import at.linuxtage.glt.db.DatabaseManager;

public class FosdemApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		DatabaseManager.init(this);
		// Initialize settings
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		// Alarms (requires settings)
		FosdemAlarmManager.init(this);
	}
}
