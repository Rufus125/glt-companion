package at.linuxtage.companion.activities;

import android.content.Intent;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import at.linuxtage.companion.R;
import at.linuxtage.companion.fragments.EventDetailsFragment;
import at.linuxtage.companion.fragments.RoomImageDialogFragment;
import at.linuxtage.companion.fragments.TrackScheduleListFragment;
import at.linuxtage.companion.model.Day;
import at.linuxtage.companion.model.Event;
import at.linuxtage.companion.model.Track;
import at.linuxtage.companion.utils.NfcUtils;
import at.linuxtage.companion.utils.NfcUtils.CreateNfcAppDataCallback;
import at.linuxtage.companion.utils.ThemeUtils;
import at.linuxtage.companion.viewmodels.BookmarkStatusViewModel;
import at.linuxtage.companion.widgets.BookmarkStatusAdapter;

/**
 * Track Schedule container, works in both single pane and dual pane modes.
 *
 * @author Christophe Beyls
 */
public class TrackScheduleActivity extends AppCompatActivity
		implements TrackScheduleListFragment.Callbacks, CreateNfcAppDataCallback {

	public static final String EXTRA_DAY = "day";
	public static final String EXTRA_TRACK = "track";
	// Optional extra used as a hint for up navigation from an event
	public static final String EXTRA_FROM_EVENT_ID = "from_event_id";

	private Day day;
	private Track track;
	private boolean isTabletLandscape;
	private Event lastSelectedEvent;

	private BookmarkStatusViewModel bookmarkStatusViewModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_schedule);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		Bundle extras = getIntent().getExtras();
		day = extras.getParcelable(EXTRA_DAY);
		track = extras.getParcelable(EXTRA_TRACK);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(track.toString());
		bar.setSubtitle(day.toString());
		setTitle(String.format("%1$s, %2$s", track.toString(), day.toString()));
		ThemeUtils.setStatusBarTrackColor(this, track.getType());
		final int trackColor = ContextCompat.getColor(this, track.getType().getColorResId());
		toolbar.setBackgroundColor(trackColor);

		isTabletLandscape = getResources().getBoolean(R.bool.tablet_landscape);

		FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState == null) {
			long fromEventId = extras.getLong(EXTRA_FROM_EVENT_ID, -1L);
			final TrackScheduleListFragment trackScheduleListFragment;
			if (fromEventId != -1L) {
				trackScheduleListFragment = TrackScheduleListFragment.newInstance(day, track, fromEventId);
			} else {
				trackScheduleListFragment = TrackScheduleListFragment.newInstance(day, track);
			}
			fm.beginTransaction().add(R.id.schedule, trackScheduleListFragment).commit();
		} else {
			// Cleanup after switching from dual pane to single pane mode
			if (!isTabletLandscape) {
				FragmentTransaction ft = null;

				Fragment eventDetailsFragment = fm.findFragmentById(R.id.event);
				if (eventDetailsFragment != null) {
					ft = fm.beginTransaction();
					ft.remove(eventDetailsFragment);
				}

				Fragment roomImageDialogFragment = fm.findFragmentByTag(RoomImageDialogFragment.TAG);
				if (roomImageDialogFragment != null) {
					if (ft == null) {
						ft = fm.beginTransaction();
					}
					ft.remove(roomImageDialogFragment);
				}

				if (ft != null) {
					ft.commit();
				}
			}
		}

		if (isTabletLandscape) {
			ImageButton floatingActionButton = findViewById(R.id.fab);
			if (floatingActionButton != null) {
				bookmarkStatusViewModel = ViewModelProviders.of(this).get(BookmarkStatusViewModel.class);
				BookmarkStatusAdapter.setupWithImageButton(bookmarkStatusViewModel, this, floatingActionButton);
			}

			// Enable Android Beam
			NfcUtils.setAppDataPushMessageCallbackIfAvailable(this, this);
		}
	}

	@Nullable
	@Override
	public Intent getSupportParentActivityIntent() {
		final Intent intent = super.getSupportParentActivityIntent();
		// Add FLAG_ACTIVITY_SINGLE_TOP to ensure the Main activity in the back stack is not re-created
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return intent;
	}

	// TrackScheduleListFragment.Callbacks

	@Override
	public void onEventSelected(int position, Event event) {
		if (isTabletLandscape) {
			// Tablet mode: Show event details in the right pane fragment
			lastSelectedEvent = event;

			FragmentManager fm = getSupportFragmentManager();
			EventDetailsFragment currentFragment = (EventDetailsFragment) fm.findFragmentById(R.id.event);
			if (event != null) {
				// Only replace the fragment if the event is different
				if ((currentFragment == null) || !currentFragment.getEvent().equals(event)) {
					Fragment f = EventDetailsFragment.newInstance(event);
					// Allow state loss since the event fragment will be synchronized with the list selection after activity re-creation
					fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.event, f).commitAllowingStateLoss();
				}
			} else {
				// Nothing is selected because the list is empty
				if (currentFragment != null) {
					fm.beginTransaction().remove(currentFragment).commitAllowingStateLoss();
				}
			}

			if (bookmarkStatusViewModel != null) {
				bookmarkStatusViewModel.setEvent(event);
			}
		} else {
			// Classic mode: Show event details in a new activity
			Intent intent = new Intent(this, TrackScheduleEventActivity.class);
			intent.putExtra(TrackScheduleEventActivity.EXTRA_DAY, day);
			intent.putExtra(TrackScheduleEventActivity.EXTRA_TRACK, track);
			intent.putExtra(TrackScheduleEventActivity.EXTRA_POSITION, position);
			startActivity(intent);
		}
	}

	// CreateNfcAppDataCallback

	@Override
	public NdefRecord createNfcAppData() {
		if (lastSelectedEvent == null) {
			return null;
		}
		return NfcUtils.createEventAppData(this, lastSelectedEvent);
	}
}
