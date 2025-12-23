package main;

import browser.NgordnetQueryHandler;

public class AutograderBuddy {
    /** Returns a HyponymsHandler. */
    public static NgordnetQueryHandler getHyponymsHandler(
            String wordFile, String countFile,
            String synsetFile, String hyponymFile) {
        return new HyponymsHandler(synsetFile, hyponymFile, wordFile, countFile);
    }
}
