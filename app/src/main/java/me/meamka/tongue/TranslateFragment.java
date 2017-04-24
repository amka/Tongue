package me.meamka.tongue;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.studioidan.httpagent.JsonCallback;
import com.studioidan.httpagent.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TranslateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslateFragment extends Fragment {

    private Translator translator;

    private EditText originTextView;
    private TextView transText;
    private Button originLangBtn;
    private Button targetLangBtn;
    private ImageButton swapLangBtn;
    private String originLanguage;
    private String targetLanguage;

    Map<String, String> langMap;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TranslateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TranslateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TranslateFragment newInstance(String param1, String param2) {
        TranslateFragment fragment = new TranslateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        translator = new Translator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_translate, container, false);

        // Find UI buttons
        originLangBtn = (Button) view.findViewById(R.id.originLangBtn);
        targetLangBtn = (Button) view.findViewById(R.id.targetLangBtn);
        swapLangBtn = (ImageButton) view.findViewById(R.id.swapLangBtn);

        // connect buttons with handlers
        originLangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TONGUE", "Origin Clicked");
            }
        });

        swapLangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TONGUE", "Swapped!");
            }
        });

        targetLangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (langMap == null || langMap.isEmpty()) {
                    Toast.makeText(
                            getContext(),
                            "No languages available. Check your Internet connection",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }
                List<String> keys = new ArrayList<String>(langMap.keySet());
                final ArrayAdapter<String> langsAdapter = new ArrayAdapter<String>(
                        getActivity().getApplicationContext(),
                        R.layout.language_list_item,
                        R.id.langText,
                        keys);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.select_language_title);

                builder.setAdapter(langsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        targetLanguage = langsAdapter.getItem(which);
                        Log.d("TONGUE", "Selected lang code -> " + langMap.get(targetLanguage));
                    }
                });

                AlertDialog langDialog = builder.create();
                langDialog.show();
            }
        });

        // Load available languages
        new Translator().getLangs(
                Resources.getSystem().getConfiguration().locale.getLanguage(),
                new JsonCallback() {
                    @Override
                    protected void onDone(boolean success, JSONObject jsonResults) {
                        if (success) {
                            langMap = new HashMap<String, String>();
                            try {
                                for (Iterator<String> iter = jsonResults.getJSONObject("langs").keys(); iter.hasNext(); ) {
                                    String key = (String) iter.next();
                                    String value = jsonResults.getJSONObject("langs").getString(key);
                                    langMap.put(value, key);
                                }

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("langs", (Serializable) langMap);
                            } catch (JSONException e) {

                                Toast toast = Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        "Looks like Internet is not available yet!",
                                        Toast.LENGTH_SHORT
                                );
                                toast.show();
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        transText = (TextView) view.findViewById(R.id.transText);

        // Connect change event handler with text edit view
        originTextView = (EditText) view.findViewById(R.id.originText);
        originTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Auto generated. Not used yet.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Delay translation call to get time for user's input
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // Check if there is string to translate
                        String originText = originTextView.getText().toString();
                        if (originText.length() <= 0) {
                            transText.setText("");
                            return;
                        }

                        // Call API method with callback
                        translator.getTranslation(originText, langMap.get(targetLanguage), new JsonCallback() {
                            @Override
                            protected void onDone(boolean success, JSONObject jsonResults) {
                                Log.d("API", "Success -> " + jsonResults.toString());
                                if (success) {
                                    try {
                                        transText.setText(jsonResults.getJSONArray("text").getString(0));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                }, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Auto generated. Not used yet.
            }
        });

        // Add handler to put translated text into History storage
        originTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("TONGUE", String.format("Add \"%s\" to History storage", originTextView.getText()));
                }
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
