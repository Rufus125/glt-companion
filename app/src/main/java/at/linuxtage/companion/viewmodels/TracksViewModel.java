package at.linuxtage.companion.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import at.linuxtage.companion.db.AppDatabase;
import at.linuxtage.companion.model.Day;
import at.linuxtage.companion.model.Track;

import java.util.List;

public class TracksViewModel extends AndroidViewModel {

	private final AppDatabase appDatabase = AppDatabase.getInstance(getApplication());
	private final MutableLiveData<Day> day = new MutableLiveData<>();
	private final LiveData<List<Track>> tracks = Transformations.switchMap(day,
			day -> appDatabase.getScheduleDao().getTracks(day));

	public TracksViewModel(@NonNull Application application) {
		super(application);
	}

	public void setDay(@NonNull Day day) {
		if (!day.equals(this.day.getValue())) {
			this.day.setValue(day);
		}
	}

	public LiveData<List<Track>> getTracks() {
		return tracks;
	}
}
