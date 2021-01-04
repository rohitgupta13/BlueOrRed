package com.blipthirteen.twocars.adhandler;

public class MockAdHandler implements AdHandler{

    @Override
    public void showAds(boolean show) {
        System.out.println("Showing ads: " + show);
    }

    @Override
    public boolean isInterstitialLoaded() {
        return false;
    }

    @Override
    public void showInterstitial() {
        System.out.println("Showing Interstitial ads");
    }
}
