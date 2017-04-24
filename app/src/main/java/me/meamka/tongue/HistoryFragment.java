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

import me.meamka.tongue.Storage.HistoryArrayAdapter;
import me.meamka.tongue.Storage.HistoryDBHelper;
import me.meamka.tongue.Storage.HistoryEntry;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class HistoryFragment extends ListFragment {

    private HistoryDBHelper historyDBHelper;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistoryFragment newInstance(int columnCount) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        historyDBHelper = new HistoryDBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        final List<HistoryEntry> allEntries = historyDBHelper.getAllEntries(20);

        Log.d("TONGUE", allEntries.toString());
        ArrayAdapter<HistoryEntry> entriesAdapter = new HistoryArrayAdapter(
                getActivity(),
                R.layout.fragment_history_list_item,
                allEntries
        );
        setListAdapter(entriesAdapter);

        // Set handler to clear storage
        ImageButton clearHistoryButton = (ImageButton) view.findViewById(R.id.deleteAllHistoryBtn);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDBHelper.deleteAll();

                Toast.makeText(
                        getContext(),
                        "History cleared.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        historyDBHelper = null;
    }
}
