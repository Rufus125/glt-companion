package at.linuxtage.companion.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import at.linuxtage.companion.R;
import at.linuxtage.companion.fragments.ExternalBookmarksListFragment;
import at.linuxtage.companion.utils.NfcUtils;

public class ExternalBookmarksActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			Intent intent = getIntent();
			long[] bookmarkIds = null;
			if (NfcUtils.hasAppData(intent)) {
				bookmarkIds = NfcUtils.toBookmarks(NfcUtils.extractAppData(intent));
			}
			if (bookmarkIds == null) {
				// Invalid data format, exit
				finish();
				return;
			}

			Fragment f = ExternalBookmarksListFragment.newInstance(bookmarkIds);
			getSupportFragmentManager().beginTransaction().add(R.id.content, f).commit();
		}
	}
}
