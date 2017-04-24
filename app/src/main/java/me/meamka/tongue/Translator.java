package me.meamka.tongue;

import com.studioidan.httpagent.HttpAgent;
import com.studioidan.httpagent.JsonCallback;

/**
 * Created by andrey.maksimov on 23.04.17.
 */

public class Translator {

    private final String API_KEY = "trnsl.1.1.20170420T225006Z.7137c2209714740b.92c202adfd0c51bdf6d20be8fb8e86416fe3edd5";

    private final String API_BASE = "https://translate.yandex.net/api/v1.5/tr.json/";

    public void getTranslation(String origin, String lang, JsonCallback callback) {
        final String API_METHOD = "translate";

        HttpAgent.get(String.format("%s%s", API_BASE, API_METHOD))
                .queryParams("key", API_KEY, "text", origin, "lang", lang)
                .goJson(callback);
    }

    public void getLangs(String ui, JsonCallback callback) {
        final String API_METHOD = "getLangs";

        HttpAgent.get(String.format("%s%s", API_BASE, API_METHOD))
                .queryParams("key", API_KEY, "ui", ui)
                .goJson(callback);
    }
}
