package at.linuxtage.companion;

import android.app.Application;
import androidx.preference.PreferenceManager;
import at.linuxtage.companion.alarms.FosdemAlarmManager;

public class GLTApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize settings
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		// Alarms (requires settings)
		FosdemAlarmManager.init(this);
	}
}
