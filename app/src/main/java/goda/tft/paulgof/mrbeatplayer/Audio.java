package goda.tft.paulgof.mrbeatplayer;

import java.io.Serializable;

public class Audio implements Serializable {

    private String data;
    private String title;
    private String id;
    private String artist;

    public Audio(String data, String title, String id, String artist) {
        this.data = data;
        this.title = title;
        this.id = id;
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


}
