package at.linuxtage.companion.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import at.linuxtage.companion.BuildConfig;
import at.linuxtage.companion.db.DatabaseManager;
import at.linuxtage.companion.model.Event;
import be.digitalia.fosdem.model.RoomStatus;
import at.linuxtage.companion.parsers.EventsParser;
import at.linuxtage.companion.utils.HttpUtils;

/**
 * Main API entry point.
 *
 * @author Christophe Beyls
 */
public class GLTApi {

	// Local broadcasts parameters
	public static final String ACTION_DOWNLOAD_SCHEDULE_RESULT = BuildConfig.APPLICATION_ID + ".action.DOWNLOAD_SCHEDULE_RESULT";
	public static final String EXTRA_RESULT = "RESULT";

	public static final int RESULT_ERROR = -1;
	public static final int RESULT_UP_TO_DATE = -2;

	private static final Lock scheduleLock = new ReentrantLock();
	private static final MutableLiveData<Integer> progress = new MutableLiveData<>();
	private static LiveData<Map<String, RoomStatus>> roomStatuses;

	/**
	 * Download & store the schedule to the database.
	 * Only one thread at a time will perform the actual action, the other ones will return immediately.
	 * The result will be sent back in the form of a local broadcast with an ACTION_DOWNLOAD_SCHEDULE_RESULT action.
	 */
	@WorkerThread
	public static void downloadSchedule(Context context) {
		if (!scheduleLock.tryLock()) {
			// If a download is already in progress, return immediately
			return;
		}

		progress.postValue(-1);
		int result = RESULT_ERROR;
		try {
			DatabaseManager dbManager = DatabaseManager.getInstance();
			HttpUtils.HttpResult httpResult = HttpUtils.get(
					GLTUrls.getSchedule(),
					dbManager.getLastModifiedTag(),
					new HttpUtils.ProgressUpdateListener() {
						@Override
						public void onProgressUpdate(int percent) {
							progress.postValue(percent);
						}
					});
			if (httpResult.inputStream == null) {
				// Nothing to parse, the result is up-to-date.
				result = RESULT_UP_TO_DATE;
				return;
			}

			try {
				Iterable<Event> events = new EventsParser().parse(httpResult.inputStream);
				result = dbManager.storeSchedule(events, httpResult.lastModified);
			} finally {
				try {
					httpResult.inputStream.close();
				} catch (Exception ignored) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			progress.postValue(100);
			Intent resultIntent = new Intent(ACTION_DOWNLOAD_SCHEDULE_RESULT)
					.putExtra(EXTRA_RESULT, result);
			LocalBroadcastManager.getInstance(context).sendBroadcast(resultIntent);
			scheduleLock.unlock();
		}
	}

	/**
	 * @return The current schedule download progress:
	 * -1   : in progress, indeterminate
	 * 0..99: progress value
	 * 100  : download complete or inactive
	 */
	public static LiveData<Integer> getDownloadScheduleProgress() {
		return progress;
	}

	@MainThread
	public static LiveData<Map<String, RoomStatus>> getRoomStatuses() {
		if (roomStatuses == null) {
			// The room statuses will only be loaded when the event is live.
			// RoomStatusesLiveData uses the days from the database to determine it.
			roomStatuses = new RoomStatusesLiveData(DatabaseManager.getInstance().getDays());
			// Implementors: replace the above live with the next one to disable room status support
			// roomStatuses = new MutableLiveData<>();
		}
		return roomStatuses;
	}
}
