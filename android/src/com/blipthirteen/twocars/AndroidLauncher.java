package com.blipthirteen.twocars;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.blipthirteen.twocars.adhandler.AdHandler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import de.golfgl.gdxgamesvcs.GpgsClient;


public class AndroidLauncher extends AndroidApplication implements AdHandler {

	private static final String TAG = "AndroidLauncher";
	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;
	protected AdView adView;
	private InterstitialAd mInterstitialAd;


	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case SHOW_ADS:
					adView.setVisibility(View.VISIBLE);
					break;
				case HIDE_ADS:
					adView.setVisibility(View.GONE);
					break;
			}
		}
	};

	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		GpgsClient gpgs = new GpgsClient().initialize(this,false);

		AdSize adSize = getAdSize();
		TwoCars twoCars = new TwoCars(gpgs, this, adSize.getHeightInPixels(this) + 15);

		View gameView = initializeForView(twoCars, config);
		layout.addView(gameView);

		adView = new AdView(this);
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				int visiblity = adView.getVisibility();
				adView.setVisibility(AdView.GONE);
				adView.setVisibility(visiblity);
				Log.i(TAG, "Ad Loaded...");
			}
		});

		adView.setAdSize(adSize);

		adView.setAdUnitId("ca-app-pub-7608358950241074/2111470511");
		//adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");// Test ad

		AdRequest.Builder builder = new AdRequest.Builder();
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		layout.addView(adView, adParams);
		adView.loadAd(builder.build());
		setContentView(layout);

		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-7608358950241074/9545642362");
		//mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //Test
		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {}

			@Override
			public void onAdClosed() {
				loadIntersitialAd();
			}
		});
		loadIntersitialAd();
	}

	private void loadIntersitialAd(){
		AdRequest interstitialRequest = new AdRequest.Builder().build();
		mInterstitialAd.loadAd(interstitialRequest);
	}

	@Override
	public void showInterstitial() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mInterstitialAd.isLoaded())
					mInterstitialAd.show();
				else
					loadIntersitialAd();
			}
		});
	}

	@Override
	public boolean isInterstitialLoaded() {
		return mInterstitialAd.isLoaded();
	}

	private AdSize getAdSize() {
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float widthPixels = outMetrics.widthPixels;
		float density = outMetrics.density;

		int adWidth = (int) (widthPixels / density);
		return AdSize.getPortraitAnchoredAdaptiveBannerAdSize (this, adWidth);
	}
}