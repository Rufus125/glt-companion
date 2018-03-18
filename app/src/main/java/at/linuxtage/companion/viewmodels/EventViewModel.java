package at.linuxtage.companion.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import at.linuxtage.companion.db.DatabaseManager;
import at.linuxtage.companion.livedata.AsyncTaskLiveData;
import at.linuxtage.companion.model.Event;

public class EventViewModel extends ViewModel {

	private long eventId = -1L;

	private final AsyncTaskLiveData<Event> event = new AsyncTaskLiveData<Event>() {
		@Override
		protected Event loadInBackground() throws Exception {
			return DatabaseManager.getInstance().getEvent(eventId);
		}
	};

	public boolean hasEventId() {
		return this.eventId != -1L;
	}

	public void setEventId(long eventId) {
		if (this.eventId != eventId) {
			this.eventId = eventId;
			event.forceLoad();
		}
	}

	public LiveData<Event> getEvent() {
		return event;
	}
}
