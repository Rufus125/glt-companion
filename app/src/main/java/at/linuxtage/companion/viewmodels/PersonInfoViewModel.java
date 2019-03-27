package at.linuxtage.companion.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import at.linuxtage.companion.db.AppDatabase;
import at.linuxtage.companion.model.Person;
import at.linuxtage.companion.model.StatusEvent;

public class PersonInfoViewModel extends AndroidViewModel {

	private final AppDatabase appDatabase = AppDatabase.getInstance(getApplication());
	private final MutableLiveData<Person> person = new MutableLiveData<>();
	private final LiveData<PagedList<StatusEvent>> events = Transformations.switchMap(person,
			new Function<Person, LiveData<PagedList<StatusEvent>>>() {
				@Override
				public LiveData<PagedList<StatusEvent>> apply(Person person) {
					return new LivePagedListBuilder<>(appDatabase.getScheduleDao().getEvents(person), 20)
							.build();
				}
			});

	public PersonInfoViewModel(@NonNull Application application) {
		super(application);
	}

	public void setPerson(@NonNull Person person) {
		if (!person.equals(this.person.getValue())) {
			this.person.setValue(person);
		}
	}

	public LiveData<PagedList<StatusEvent>> getEvents() {
		return events;
	}
}
