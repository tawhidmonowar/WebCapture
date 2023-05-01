package com.prostudio.webtopdf;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

/**
 * Prefetches App Open Ads.
 */
public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
   private static final String LOG_TAG = "AppOpenManager";
   private static final String AD_UNIT_ID = "ca-app-pub-5151906942553409/3355507297";
   private static boolean isShowingAd = false;
   private final MyApplication myApplication;
   private AppOpenAd appOpenAd = null;
   private AppOpenAd.AppOpenAdLoadCallback loadCallback;
   private Activity currentActivity;
   private long loadTime = 0;
   private boolean temp = true;

   public AppOpenManager(MyApplication myApplication) {
      this.myApplication = myApplication;
      this.myApplication.registerActivityLifecycleCallbacks(this);
      ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
   }
   /**
    * Shows the ad if one isn't already showing.
    */
   public void showAdIfAvailable() {
      // Only show ad if there is not already an app open ad currently showing
      // and an ad is available.
      if (!isShowingAd && isAdAvailable()) {
         Log.d(LOG_TAG, "Will show ad.");

         FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
               // Set the reference to null so isAdAvailable() returns false.
               AppOpenManager.this.appOpenAd = null;
               isShowingAd = false;
               fetchAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
            }

            @Override
            public void onAdShowedFullScreenContent() {
               isShowingAd = true;
            }
         };

         appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
         appOpenAd.show(currentActivity);

      } else {
         Log.d(LOG_TAG, "Can not show ad.");
         fetchAd();
      }
   }

   /**
    * LifecycleObserver methods
    */
   @OnLifecycleEvent(ON_START)
   public void onStart() {
      showAdIfAvailable();
      Log.d(LOG_TAG, "onStart");
   }

   /**
    * Request an ad
    */
   public void fetchAd() {
      // Have unused ad, no need to fetch another.
      if (isAdAvailable()) {
         return;
      }

      loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {

         @Override
         public void onAdLoaded(AppOpenAd ad) {
            AppOpenManager.this.appOpenAd = ad;
            AppOpenManager.this.loadTime = (new Date()).getTime();

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
               @Override
               public void onAdDismissedFullScreenContent() {
                  // Set the reference to null so isAdAvailable() returns false.
                  AppOpenManager.this.appOpenAd = null;
                  isShowingAd = false;
                  fetchAd();
               }

               @Override
               public void onAdFailedToShowFullScreenContent(AdError adError) {
               }

               @Override
               public void onAdShowedFullScreenContent() {
                  isShowingAd = true;
               }
            };

            if(temp) {
               appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
               appOpenAd.show(currentActivity);
               temp=false;
            }

         }

         @Override
         public void onAdFailedToLoad(LoadAdError loadAdError) {
            // Handle the error.
            Log.d(LOG_TAG, "failed to load");
         }

//                    @Override
//                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
//                        super.onAdLoaded(appOpenAd);
//                        AppOpenManager.this.appOpenAd = appOpenAd;
//                        AppOpenManager.this.loadTime = (new Date()).getTime();
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        super.onAdFailedToLoad(loadAdError);
//                        Log.d(LOG_TAG, "failed to load");
//                    }

      };
      AdRequest request = getAdRequest();
      AppOpenAd.load(
              myApplication, AD_UNIT_ID, request,
              AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
   }

   /**
    * Creates and returns ad request.
    */
   private AdRequest getAdRequest() {
      return new AdRequest.Builder().build();
   }

   /**
    * Utility method to check if ad was loaded more than n hours ago.
    */
   private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
      long dateDifference = (new Date()).getTime() - this.loadTime;
      long numMilliSecondsPerHour = 3600000;
      return (dateDifference < (numMilliSecondsPerHour * numHours));
   }

   /**
    * Utility method that checks if ad exists and can be shown.
    */
   public boolean isAdAvailable() {
      return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
   }

   @Override
   public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

   }

   @Override
   public void onActivityStarted(Activity activity) {
      currentActivity = activity;
   }

   @Override
   public void onActivityResumed(Activity activity) {
      currentActivity = activity;
   }

   @Override
   public void onActivityPaused(@NonNull Activity activity) {

   }

   @Override
   public void onActivityStopped(@NonNull Activity activity) {

   }

   @Override
   public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

   }

   @Override
   public void onActivityDestroyed(Activity activity) {
      currentActivity = null;
   }


}