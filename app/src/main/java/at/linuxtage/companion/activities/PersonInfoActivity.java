package at.linuxtage.companion.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import at.linuxtage.companion.R;
import at.linuxtage.companion.fragments.PersonInfoListFragment;
import at.linuxtage.companion.model.Person;

public class PersonInfoActivity extends AppCompatActivity {

	public static final String EXTRA_PERSON = "person";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_extended_title);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		Person person = getIntent().getParcelableExtra(EXTRA_PERSON);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		setTitle(person.getName());

		if (savedInstanceState == null) {
			Fragment f = PersonInfoListFragment.newInstance(person);
			getSupportFragmentManager().beginTransaction().add(R.id.content, f).commit();
		}
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
}
