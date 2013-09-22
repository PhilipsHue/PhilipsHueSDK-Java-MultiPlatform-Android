package com.philips.lighting.hue.local.sdk.demo.data;

import java.util.List;

import com.philips.lighting.model.PHScene;

/**
 * Data storage for the Sample Application.
 * 
 * @author S O'Reilly
 * 
 */
public class SampleAppData {
    private static SampleAppData instance = null;
    private List<PHScene> allScenes;

    public static SampleAppData getInstance() {
        if (instance == null) {
            instance = new SampleAppData();
        }
        return instance;
    }

    /**
     * @return the allScenes
     */
    public List<PHScene> getAllScenes() {
        return allScenes;
    }

    /**
     * @param allScenes
     *            the allScenes to set
     */
    public void setAllScenes(List<PHScene> allScenes) {
        this.allScenes = allScenes;
    }
}
