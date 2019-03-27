package at.linuxtage.companion.fragments;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import at.linuxtage.companion.R;
import at.linuxtage.companion.model.StatusEvent;
import at.linuxtage.companion.viewmodels.LiveViewModel;

public class NextLiveListFragment extends BaseLiveListFragment {

	@Override
	protected String getEmptyText() {
		return getString(R.string.next_empty);
	}

	@NonNull
	@Override
	protected LiveData<PagedList<StatusEvent>> getDataSource(@NonNull LiveViewModel viewModel) {
		return viewModel.getNextEvents();
	}
}
