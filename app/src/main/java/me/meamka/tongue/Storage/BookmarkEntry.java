package me.meamka.tongue.Storage;

/**
 * Created by andrey.maksimov on 24.04.17.
 */

public class BookmarkEntry {
    private int _id;
    private String origin;
    private String translated;
    private String originLang;
    private String targetLang;

    public BookmarkEntry() {
    }

    public BookmarkEntry(String origin, String translated, String originLang, String targetLang) {
        this.origin = origin;
        this.translated = translated;
        this.originLang = originLang;
        this.targetLang = targetLang;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String translated) {
        this.translated = translated;
    }

    public String getOriginLang() {
        return originLang;
    }

    public void setOriginLang(String originLang) {
        this.originLang = originLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }
}
