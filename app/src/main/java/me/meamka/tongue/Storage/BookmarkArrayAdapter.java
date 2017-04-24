package me.meamka.tongue.Storage;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.meamka.tongue.R;

/**
 * Created by andrey.maksimov on 24.04.17.
 */

public class BookmarkArrayAdapter extends ArrayAdapter<BookmarkEntry> {

    private final List<BookmarkEntry> list;
    private final Activity context;
    private final int resource;

    public BookmarkArrayAdapter(@NonNull Activity context, @LayoutRes int resource, List<BookmarkEntry> list) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater layoutInflater = context.getLayoutInflater();

        if (convertView == null) {
            view = layoutInflater.inflate(resource, null);
        } else {
            view = convertView;
        }
        BookmarkEntry item = list.get(position);
        if (item != null) {
            ((TextView) view.findViewById(R.id.bookmarkOriginLabel)).setText(item.getOrigin());
            ((TextView) view.findViewById(R.id.bookmarkTranslatedLabel)).setText(item.getTranslated());
            ((TextView) view.findViewById(R.id.bookmarkOriginLang)).setText(item.getOriginLang());
            ((TextView) view.findViewById(R.id.bookmarkTargetLang)).setText(item.getTargetLang());
            Log.d("TONGUE", String.format("Adapter: %d -> %s", position, item.getOrigin()));
        }
        return view;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }
}
