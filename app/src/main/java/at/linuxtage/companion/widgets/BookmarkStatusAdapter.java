package at.linuxtage.companion.widgets;

import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import at.linuxtage.companion.R;
import at.linuxtage.companion.viewmodels.BookmarkStatusViewModel;

public class BookmarkStatusAdapter {

	private BookmarkStatusAdapter() {
	}

	/**
	 * Connect an ImageButton to a BookmarkStatusViewModel
	 * to update its icon according to the current status and trigger a bookmark toggle on click.
	 */
	public static void setupWithImageButton(@NonNull final BookmarkStatusViewModel viewModel, @NonNull LifecycleOwner owner,
											@NonNull final ImageButton imageButton) {
		imageButton.setOnClickListener(v -> viewModel.toggleBookmarkStatus());
		viewModel.getBookmarkStatus().observe(owner, bookmarkStatus -> {
			if (bookmarkStatus == null) {
				imageButton.setEnabled(false);
                imageButton.setSelected(false);
			} else {
				imageButton.setEnabled(true);
                imageButton.setContentDescription(imageButton.getContext().getString(bookmarkStatus.isBookmarked() ? R.string.remove_bookmark : R.string.add_bookmark));
                imageButton.setSelected(bookmarkStatus.isBookmarked());
                // Only animate updates, when the button was already enabled
                if (!bookmarkStatus.isUpdate() || !imageButton.isEnabled()) {
                    imageButton.jumpDrawablesToCurrentState();
				}
			}
		});
	}
}
