package be.digitalia.companion.viewmodels;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import be.digitalia.companion.db.AppDatabase;
import be.digitalia.companion.model.Day;
import be.digitalia.companion.model.Track;

public class TracksViewModel extends AndroidViewModel {

	private final AppDatabase appDatabase = AppDatabase.getInstance(getApplication());
	private final MutableLiveData<Day> day = new MutableLiveData<>();
	private final LiveData<List<Track>> tracks = Transformations.switchMap(day,
			new Function<Day, LiveData<List<Track>>>() {
				@Override
				public LiveData<List<Track>> apply(Day day) {
					return appDatabase.getScheduleDao().getTracks(day);
				}
			});

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
