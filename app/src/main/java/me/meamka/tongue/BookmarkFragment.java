package me.meamka.tongue;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import me.meamka.tongue.Storage.BookmarkArrayAdapter;
import me.meamka.tongue.Storage.BookmarkDBHelper;
import me.meamka.tongue.Storage.BookmarkEntry;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class BookmarkFragment extends ListFragment {

    private BookmarkDBHelper bookmarkDBHelper;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookmarkFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BookmarkFragment newInstance(int columnCount) {
        BookmarkFragment fragment = new BookmarkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookmarkDBHelper = new BookmarkDBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        final List<BookmarkEntry> allEntries = bookmarkDBHelper.getAllEntries(20);

        Log.d("TONGUE", allEntries.toString());
        ArrayAdapter<BookmarkEntry> entriesAdapter = new BookmarkArrayAdapter(
                getActivity(),
                R.layout.fragment_bookmark_list_item,
                allEntries
        );
        setListAdapter(entriesAdapter);

        // Set handler to clear storage
        ImageButton clearHistoryButton = (ImageButton) view.findViewById(R.id.deleteAllBookmarksBtn);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarkDBHelper.deleteAll();
                allEntries.clear();
                Toast.makeText(
                        getContext(),
                        "Bookmarks cleared",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bookmarkDBHelper = null;
    }
}
