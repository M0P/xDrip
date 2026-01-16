package com.eveningoutpost.dexdrip.plugin;

import static com.eveningoutpost.dexdrip.models.JoH.msSince;
import static com.eveningoutpost.dexdrip.models.JoH.tsl;
import static com.eveningoutpost.dexdrip.utilitymodels.Constants.MINUTE_IN_MS;
import static com.eveningoutpost.dexdrip.plugin.PluginDef.State.Fresh;
import static com.eveningoutpost.dexdrip.plugin.PluginDef.State.Loaded;
import static com.eveningoutpost.dexdrip.plugin.PluginDef.State.Loading;

/**
 * JamOrHam
 *
 * Plugin meta-data and state handling
 */

public class PluginDef {

    final String name;
    final String author;
    final String version;
    final String repository;
    private volatile State state = Fresh;
    private volatile long lastChange;

    public PluginDef(String name, String author, String version, String repository) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.repository = repository;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public String getRepository() {
        return repository;
    }

    public boolean isReady() {
        return state == Loaded;
    }

    public boolean isFresh() {
        return state == Fresh;
    }

    public boolean isLoaded() {
        return state == Loaded;
    }

    public boolean loadingFailed() {
        return state == Loading && (msSince(lastChange) > MINUTE_IN_MS * 5);
    }

    private void changeState(final State state) {
        lastChange = tsl();
        this.state = state;
    }

    public void setLoading() {
        changeState(Loading);
    }

    public void setLoaded() {
        changeState(Loaded);
    }

    public void reset() {
        state = Fresh;
    }

    public String canonical() {
        return author + "-" + name + "-" + version;
    }

    public String pname() {
        return author + "." + name + ".";
    }

    public enum State {
        Fresh,
        Loading,
        Loaded,
    }

}
