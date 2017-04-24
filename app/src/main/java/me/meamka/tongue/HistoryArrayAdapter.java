package me.meamka.tongue;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.meamka.tongue.Storage.HistoryEntry;

/**
 * Created by andrey.maksimov on 24.04.17.
 */

public class HistoryArrayAdapter extends ArrayAdapter<HistoryEntry> {

    private final List<HistoryEntry> list;
    private final Activity context;
    private final int resource;

    public HistoryArrayAdapter(@NonNull Activity context, @LayoutRes int resource, List<HistoryEntry> list) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater layoutInflater = context.getLayoutInflater();

        if(convertView == null) {
            view = layoutInflater.inflate(resource, null);
        } else {
            view = convertView;
        }
        HistoryEntry item = list.get(position);
        if (item != null) {
            ((TextView) view.findViewById(R.id.historyOriginLabel)).setText(item.getOrigin());
            ((TextView) view.findViewById(R.id.historyTranslatedLabel)).setText(item.getTranslated());
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
