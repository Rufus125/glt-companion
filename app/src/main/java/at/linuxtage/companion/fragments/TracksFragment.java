package at.linuxtage.companion.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import at.linuxtage.companion.R;
import at.linuxtage.companion.db.DatabaseManager;
import at.linuxtage.companion.model.Day;
import at.linuxtage.companion.widgets.SlidingTabLayout;

public class TracksFragment extends Fragment implements RecycledViewPoolProvider, Observer<List<Day>> {

	static class ViewHolder {
		View contentView;
		View emptyView;
		ViewPager pager;
		SlidingTabLayout slidingTabs;
		DaysAdapter daysAdapter;
		RecyclerView.RecycledViewPool recycledViewPool;
	}

	private static final String PREF_CURRENT_PAGE = "tracks_current_page";

	private ViewHolder holder;
	private int savedCurrentPage = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// Restore the current page from preferences
			savedCurrentPage = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(PREF_CURRENT_PAGE, -1);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tracks, container, false);

		holder = new ViewHolder();
		holder.contentView = view.findViewById(R.id.content);
		holder.emptyView = view.findViewById(android.R.id.empty);
		holder.pager = view.findViewById(R.id.pager);
		holder.slidingTabs = view.findViewById(R.id.sliding_tabs);
		holder.daysAdapter = new DaysAdapter(getChildFragmentManager());
		holder.recycledViewPool = new RecyclerView.RecycledViewPool();

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		holder = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LiveData<List<Day>> daysLiveData = DatabaseManager.getInstance().getDays();
		daysLiveData.removeObserver(this);
		daysLiveData.observe(this, this);
	}

	@Override
	public void onStop() {
		super.onStop();
		// Save the current page to preferences if it has changed
		final int page = holder.pager.getCurrentItem();
		SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		if (prefs.getInt(PREF_CURRENT_PAGE, -1) != page) {
			prefs.edit()
					.putInt(PREF_CURRENT_PAGE, page)
					.apply();
		}
	}

	@Override
	public RecyclerView.RecycledViewPool getRecycledViewPool() {
		return (holder == null) ? null : holder.recycledViewPool;
	}

	@Override
	public void onChanged(@Nullable List<Day> days) {
		holder.daysAdapter.setDays(days);

		final int totalPages = holder.daysAdapter.getCount();
		if (totalPages == 0) {
			holder.contentView.setVisibility(View.GONE);
			holder.emptyView.setVisibility(View.VISIBLE);
		} else {
			holder.contentView.setVisibility(View.VISIBLE);
			holder.emptyView.setVisibility(View.GONE);
			if (holder.pager.getAdapter() == null) {
				holder.pager.setAdapter(holder.daysAdapter);
				holder.slidingTabs.setViewPager(holder.pager);
			}
			if (savedCurrentPage != -1) {
				holder.pager.setCurrentItem(Math.min(savedCurrentPage, totalPages - 1), false);
				savedCurrentPage = -1;
			}
		}
	}

	private static class DaysAdapter extends FragmentStatePagerAdapter {

		private List<Day> days;

		public DaysAdapter(FragmentManager fm) {
			super(fm);
		}

		public void setDays(List<Day> days) {
			if (this.days != days) {
				this.days = days;
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			return (days == null) ? 0 : days.size();
		}

		@Override
		public Fragment getItem(int position) {
			return TracksListFragment.newInstance(days.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return days.get(position).toString();
		}

		@NonNull
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// Allow the non-primary fragments to start as soon as they are visible
			Fragment f = (Fragment) super.instantiateItem(container, position);
			f.setUserVisibleHint(true);
			return f;
		}
	}
}
