package at.linuxtage.companion.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import at.linuxtage.companion.db.DatabaseManager;
import at.linuxtage.companion.livedata.AsyncTaskLiveData;
import at.linuxtage.companion.model.Event;
import at.linuxtage.companion.model.Link;
import at.linuxtage.companion.model.Person;
import at.linuxtage.companion.utils.ArrayUtils;

public class EventDetailsViewModel extends AndroidViewModel {

	public static class EventDetails {
		public List<Person> persons;
		public List<Link> links;
	}

	private Event event = null;

	private final AsyncTaskLiveData<Boolean> bookmarkStatus = new AsyncTaskLiveData<Boolean>() {

		@Override
		protected Boolean loadInBackground() throws Exception {
			return DatabaseManager.getInstance().isBookmarked(event);
		}
	};
	private final AsyncTaskLiveData<EventDetails> eventDetails = new AsyncTaskLiveData<EventDetails>() {

		@Override
		protected EventDetails loadInBackground() throws Exception {
			EventDetails result = new EventDetails();
			DatabaseManager dbm = DatabaseManager.getInstance();
			result.persons = dbm.getPersons(event);
			result.links = dbm.getLinks(event);
			return result;
		}
	};

	private final BroadcastReceiver addBookmarkReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (event.getId() == intent.getLongExtra(DatabaseManager.EXTRA_EVENT_ID, -1L)) {
				bookmarkStatus.setValue(true);
			}
		}
	};
	private final BroadcastReceiver removeBookmarksReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			long[] eventIds = intent.getLongArrayExtra(DatabaseManager.EXTRA_EVENT_IDS);
			if (ArrayUtils.indexOf(eventIds, event.getId()) != -1) {
				bookmarkStatus.setValue(false);
			}
		}
	};

	public EventDetailsViewModel(@NonNull Application application) {
		super(application);
	}

	public void setEvent(@NonNull Event event) {
		if (this.event == null) {
			this.event = event;

			bookmarkStatus.forceLoad();
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplication());
			lbm.registerReceiver(addBookmarkReceiver, new IntentFilter(DatabaseManager.ACTION_ADD_BOOKMARK));
			lbm.registerReceiver(removeBookmarksReceiver, new IntentFilter(DatabaseManager.ACTION_REMOVE_BOOKMARKS));

			eventDetails.forceLoad();
		}
	}

	public LiveData<Boolean> getBookmarkStatus() {
		return bookmarkStatus;
	}

	public void toggleBookmarkStatus() {
		Boolean isBookmarked = bookmarkStatus.getValue();
		if (isBookmarked != null) {
			new ToggleBookmarkAsyncTask(event).execute(isBookmarked);
		}
	}

	private static class ToggleBookmarkAsyncTask extends AsyncTask<Boolean, Void, Void> {

		private final Event event;

		public ToggleBookmarkAsyncTask(Event event) {
			this.event = event;
		}

		@Override
		protected Void doInBackground(Boolean... remove) {
			if (remove[0]) {
				DatabaseManager.getInstance().removeBookmark(event);
			} else {
				DatabaseManager.getInstance().addBookmark(event);
			}
			return null;
		}
	}

	public LiveData<EventDetails> getEventDetails() {
		return eventDetails;
	}

	@Override
	protected void onCleared() {
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplication());
		lbm.unregisterReceiver(addBookmarkReceiver);
		lbm.unregisterReceiver(removeBookmarksReceiver);
	}
}
