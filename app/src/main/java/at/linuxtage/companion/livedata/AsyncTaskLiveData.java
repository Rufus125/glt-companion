package at.linuxtage.companion.livedata;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

/**
 * A LiveData implementation with the same basic functionality as AsyncTaskLoader.
 */
public abstract class AsyncTaskLiveData<T> extends MutableLiveData<T> {

	private boolean contentChanged = false;
	AsyncTask<Void, Void, T> task = null;

	@MainThread
	public void onContentChanged() {
		if (hasActiveObservers()) {
			forceLoad();
		} else {
			contentChanged = true;
		}
	}

	@MainThread
	public boolean cancelLoad() {
		if (task != null) {
			boolean result = task.cancel(false);
			task = null;
			return result;
		}
		return false;
	}

	@Override
	protected void onActive() {
		if (contentChanged) {
			contentChanged = false;
			forceLoad();
		}
	}

	@Override
	public void setValue(T value) {
		// Setting a value will cancel any pending AsyncTask
		contentChanged = false;
		cancelLoad();
		super.setValue(value);
	}

	@MainThread
	@SuppressLint("StaticFieldLeak")
	public void forceLoad() {
		cancelLoad();
		task = new AsyncTask<Void, Void, T>() {

			private Throwable error;

			@Override
			protected T doInBackground(Void... voids) {
				try {
					return loadInBackground();
				} catch (Throwable e) {
					error = e;
					return null;
				}
			}

			@Override
			protected void onPostExecute(T result) {
				task = null;
				if (error == null) {
					onSuccess(result);
				} else {
					onError(error);
				}
			}
		};
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	@WorkerThread
	protected abstract T loadInBackground() throws Exception;

	@MainThread
	protected void onSuccess(T result) {
		setValue(result);
	}

	/**
	 * Override this method for custom error handling.
	 */
	@MainThread
	protected void onError(Throwable error) {
		error.printStackTrace();
	}
}
