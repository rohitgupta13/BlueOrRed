package com.blipthirteen.twocars.adhandler;

public interface AdHandler {
    public void showAds(boolean show);
    boolean isInterstitialLoaded();
    void showInterstitial();
}
