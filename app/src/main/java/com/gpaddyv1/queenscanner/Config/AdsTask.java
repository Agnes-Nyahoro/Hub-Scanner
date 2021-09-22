package com.gpaddyv1.queenscanner.Config;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdExtendedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.todobom.queenscanner.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


import static android.content.Context.MODE_PRIVATE;

   /*Add this code to manifests---> no history ads

    <activity
    android:name="com.google.android.gms.ads.AdActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
    android:noHistory="true" />

    <activity
    android:name="com.facebook.ads.AudienceNetworkActivity"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:noHistory="true" />

    <meta-data
    android:name="com.google.android.gms.ads.AD_MANAGER_APP"
    android:value="true" />
     */


public class AdsTask {

    public static final String DELTA_TIME_AD_SHOW_INTERSTITIAL = "DELTA_TIME_AD_SHOW_INTERSTITIAL";
    public static final String DELTA_TIME_AD_SHOW_BANNER = "DELTA_TIME_AD_SHOW_BANNER";
    public static final String DELTA_TIME_AD_SHOW_NOTIFICATION = "DELTA_TIME_AD_SHOW_NOTIFICATION";
    //    public static final long ONE_MINUTES = 0;
    public static final long ONE_MINUTES = 60 * 1000;
    public static final long ONE_DAY = 1000 * 3600 * 24;
//    public static final long ONE_DAY = 0;

    public static final long ONE_HOUR = 3600000; //miliseconds
    public static final String ADS_CONFIG_GLOBAL_APP = "ADS_CONFIG_GLOBAL_APP";
    public static final String TIME_ADS_CONFIG_GLOBAL_APP = "TIME_ADS_CONFIG_GLOBAL_APP";
    public static final int NOT_FOUND_APP_LENGTH = 30; //"{"data":{"app":"NOT FOUND"}}";
    private Context context;

    private AdView bannerGoogle;
    private InterstitialAd interstitialAdGoogle;
    private com.facebook.ads.AdView bannerFacebook;
    private com.facebook.ads.InterstitialAd interstitialAdFacebook;
    public static String DEVICE_TEST_ADMOB = "A769DBCCFAD24AC37DBD483F9CA83A66";
    public static String DEVICE_TEST_FACEBOOK = "12a4b096-f6cb-47bf-8996-f1b96fce8131";

    public AdsTask(Context context) {
        this.context = context;
        loadInterstitialAds();
    }


    public String getInterstitialIDFacebook() {
        String idFace = context.getString(R.string.id_interstitial_facebook);
        return idFace;
    }

    public String getBannerIDAdsFacebook() {
        String idFace = context.getString(R.string.id_banner_face);
        return idFace;
    }

    public String getBannerIDAdsAdsmob() {
        String idGoogle = context.getString(R.string.id_banner_google);
        return idGoogle;
    }

    public String getInterstitialIDAdsmob() {
        String idGoogle = context.getString(R.string.id_interstitial_google);
        return idGoogle;
    }

    public String getAppIdAdsmod() {
        String idAppGoogle = context.getString(R.string.id_app_google);
        return idAppGoogle;
    }

    public boolean needShowInterstitialAds() {
        SharedPreferences prefs = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        long previous_time = prefs.getLong(DELTA_TIME_AD_SHOW_INTERSTITIAL, 0);
        if (System.currentTimeMillis() - previous_time >= ONE_MINUTES) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needShowBannerAds() {
        SharedPreferences prefs = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        long previous_time = prefs.getLong(DELTA_TIME_AD_SHOW_BANNER, 0);
        if (System.currentTimeMillis() - previous_time >= ONE_MINUTES) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needShowNotification() {
        SharedPreferences prefs = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long previous_time = prefs.getLong(DELTA_TIME_AD_SHOW_NOTIFICATION, 0);
        if (previous_time == 0) {
            previous_time = System.currentTimeMillis();
            editor.putLong(DELTA_TIME_AD_SHOW_NOTIFICATION, System.currentTimeMillis());
            editor.commit();
            return true;
        }
        if (System.currentTimeMillis() - previous_time >= ONE_DAY) {
            return true;
        } else {
            return false;
        }
    }

    //----------------------------------------------------------------------//

    public void destroyBannerAds() {
        if (bannerGoogle != null) {
            bannerGoogle.destroy();
        }
        if (bannerFacebook != null) {
            bannerFacebook.destroy();
        }
    }

    public void loadAdsInterstitialGoogle() {
        List<String> testDeviceIds = Arrays.asList(DEVICE_TEST_ADMOB);
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        interstitialAdGoogle = new InterstitialAd(context);
        interstitialAdGoogle.setAdUnitId(getInterstitialIDAdsmob());
        interstitialAdGoogle.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                //ko can vi sau khi click ads se tu dong destroy
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                SharedPreferences prefs = context.getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(DELTA_TIME_AD_SHOW_INTERSTITIAL, System.currentTimeMillis());
                editor.apply();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e("bvh", "fail ads interstitial gg " + i);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });
        interstitialAdGoogle.loadAd(new AdRequest.Builder().build());
    }

    public void showInterstitialAds() {
        if (needShowInterstitialAds()) {
            if (interstitialAdFacebook != null && interstitialAdFacebook.isAdLoaded()) {
                interstitialAdFacebook.show();
            } else if (interstitialAdGoogle != null && interstitialAdGoogle.isLoaded()) {
                interstitialAdGoogle.show();
            }

            loadInterstitialAds();
        }
    }


    public void loadInterstitialAds() {
        AdSettings.addTestDevice(DEVICE_TEST_FACEBOOK);
        interstitialAdFacebook = new com.facebook.ads.InterstitialAd(context, getInterstitialIDFacebook());
        interstitialAdFacebook.loadAd(interstitialAdFacebook.buildLoadAdConfig().withAdListener(new InterstitialAdExtendedListener() {
            @Override
            public void onInterstitialActivityDestroyed() {

            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                SharedPreferences prefs = context.getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(DELTA_TIME_AD_SHOW_INTERSTITIAL, System.currentTimeMillis());
                editor.apply();
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("bvh", "error ads interstitial face: " + adError.getErrorMessage());
                loadAdsInterstitialGoogle();
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(DELTA_TIME_AD_SHOW_BANNER, System.currentTimeMillis());
                editor.apply();
                interstitialAdFacebook.destroy();
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

            @Override
            public void onRewardedAdCompleted() {

            }

            @Override
            public void onRewardedAdServerSucceeded() {

            }

            @Override
            public void onRewardedAdServerFailed() {

            }
        })
                .build());
        interstitialAdFacebook.loadAd();
    }

    public void loadBannerAdsGoogle(final ViewGroup viewGroup) {
        List<String> testDeviceIds = Arrays.asList(DEVICE_TEST_ADMOB);
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        if (needShowBannerAds()) {
            bannerGoogle = new AdView(context);
            bannerGoogle.setAdUnitId(getBannerIDAdsAdsmob());
            bannerGoogle.setAdSize(AdSize.SMART_BANNER);
            viewGroup.removeAllViews();
            viewGroup.addView(bannerGoogle);
            bannerGoogle.setAdListener(new AdListener() {
                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(DELTA_TIME_AD_SHOW_BANNER, System.currentTimeMillis());
                    editor.apply();
                    viewGroup.removeAllViews();
                    destroyBannerAds();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.e("bvh", "fail ads banner gg " + i);
                }
            });
            bannerGoogle.loadAd(new AdRequest.Builder().build());
        }
    }

    public void loadBannerAdsFacebook(final ViewGroup viewGroup) {
        if (needShowBannerAds()) {
            bannerFacebook = new com.facebook.ads.AdView(context, "IMG_16_9_APP_INSTALL#" + getBannerIDAdsFacebook(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            bannerFacebook.setGravity(Gravity.CENTER);
            viewGroup.addView(bannerFacebook);
            bannerFacebook.loadAd(bannerFacebook.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e("bvh", "error ads banner face: " + adError.getErrorMessage());
                    loadBannerAdsGoogle(viewGroup);
                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(DELTA_TIME_AD_SHOW_BANNER, System.currentTimeMillis());
                    editor.apply();
                    destroyBannerAds();
                    viewGroup.removeAllViews();
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            })
                    .build());
            bannerFacebook.loadAd();
        }
    }

}
