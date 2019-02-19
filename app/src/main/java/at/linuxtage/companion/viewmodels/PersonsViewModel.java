package at.linuxtage.companion.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import at.linuxtage.companion.db.AppDatabase;
import at.linuxtage.companion.model.Person;

public class PersonsViewModel extends AndroidViewModel {

	private final AppDatabase appDatabase = AppDatabase.getInstance(getApplication());
	private final LiveData<PagedList<Person>> persons
			= new LivePagedListBuilder<>(appDatabase.getScheduleDao().getPersons(), 100)
			.build();

	public PersonsViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<PagedList<Person>> getPersons() {
		return persons;
	}
}
