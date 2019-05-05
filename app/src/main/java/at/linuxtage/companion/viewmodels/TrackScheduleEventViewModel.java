package at.linuxtage.companion.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import at.linuxtage.companion.db.AppDatabase;
import at.linuxtage.companion.model.Day;
import at.linuxtage.companion.model.Event;
import at.linuxtage.companion.model.Track;

import java.util.List;

public class TrackScheduleEventViewModel extends AndroidViewModel {

	private final AppDatabase appDatabase = AppDatabase.getInstance(getApplication());
	private final MutableLiveData<Pair<Day, Track>> dayTrack = new MutableLiveData<>();
	private final LiveData<List<Event>> scheduleSnapshot = Transformations.switchMap(dayTrack,
			dayTrack -> {
				final MutableLiveData<List<Event>> resultLiveData = new MutableLiveData<>();
				appDatabase.getQueryExecutor().execute(() -> {
					final List<Event> result = appDatabase.getScheduleDao().getEventsSnapshot(dayTrack.first, dayTrack.second);
					resultLiveData.postValue(result);
				});
				return resultLiveData;
			});

	public TrackScheduleEventViewModel(@NonNull Application application) {
		super(application);
	}

	public void setTrack(@NonNull Day day, @NonNull Track track) {
		Pair<Day, Track> dayTrack = Pair.create(day, track);
		if (!dayTrack.equals(this.dayTrack.getValue())) {
			this.dayTrack.setValue(dayTrack);
		}
	}

	public LiveData<List<Event>> getScheduleSnapshot() {
		return scheduleSnapshot;
	}
}
