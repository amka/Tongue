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
import java.util.Collections;
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
                selectOriginLanguage();
            }
        });

        swapLangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = originLanguage;
                changeOriginLanguage(targetLanguage, false);
                changeTargetLanguage(tmp, false);
                translateText(originTextView.getText().toString(), originLanguage, targetLanguage);
            }
        });

        targetLangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTargetLanguage();
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

                                    // Set defaults languages.
                                    // @TODO: load last used combination from prefs
                                    if (key.equals("en"))
                                        changeOriginLanguage(value, false);
                                    if (key.equals("ru"))
                                        changeTargetLanguage(value, false);
                                }

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
                        translateText(
                                originTextView.getText().toString(),
                                originLanguage,
                                targetLanguage
                        );
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
                    Log.d("TONGUE", String.format("Add \"%s\" to History storage",
                            originTextView.getText()));
                }
            }
        });
        return view;
    }

    private ArrayAdapter<String> getLanguagesAdapter() {
        List<String> keys = new ArrayList<String>(langMap.keySet());
        Collections.sort(keys);
        return new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.language_list_item,
                R.id.langText,
                keys
        );
    }

    private void selectOriginLanguage() {

        if (langMap == null || langMap.isEmpty()) {
            Toast.makeText(
                    getContext(),
                    "No languages available. Check your Internet connection",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_language_title);

        final ArrayAdapter<String> langsAdapter = getLanguagesAdapter();

        builder.setAdapter(langsAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeOriginLanguage(langsAdapter.getItem(which), true);
                Log.d("TONGUE", "Selected lang code -> " + langMap.get(targetLanguage));
            }
        });

        AlertDialog langDialog = builder.create();
        langDialog.show();
    }

    private void selectTargetLanguage() {

        if (langMap == null || langMap.isEmpty()) {
            Toast.makeText(
                    getContext(),
                    "No languages available. Check your Internet connection",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_language_title);

        final ArrayAdapter<String> langsAdapter = getLanguagesAdapter();

        builder.setAdapter(langsAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeTargetLanguage(langsAdapter.getItem(which), true);
                Log.d("TONGUE", "Selected lang code -> " + langMap.get(targetLanguage));
            }
        });

        AlertDialog langDialog = builder.create();
        langDialog.show();
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

    /**
     * Translate given text from origin language to target
     */
    private void translateText(String text, String originLanguage, String targetLanguage) {
        // Check if there is string to translate
        if (text.length() <= 0) {
            transText.setText("");
            return;
        }

        String direction = String.format("%s-%s",
                langMap.get(originLanguage),
                langMap.get(targetLanguage)
        );

        // Call API method with callback
        translator.getTranslation(text, direction, new JsonCallback() {
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

    /**
     * Change origin language and call translate method
     *
     * @param language - origin language name
     */
    private void changeOriginLanguage(String language, Boolean translateAfter) {

        originLanguage = language;
        originLangBtn.setText(originLanguage);
        if (translateAfter)
            translateText(originTextView.getText().toString(), originLanguage, targetLanguage);
    }

    /**
     * Change target language and call translate method
     *
     * @param language - target language name
     */
    private void changeTargetLanguage(String language, Boolean translateAfter) {
        targetLanguage = language;
        targetLangBtn.setText(targetLanguage);
        if (translateAfter)
            translateText(originTextView.getText().toString(), originLanguage, targetLanguage);
    }
}
