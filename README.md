[![Circle CI](https://circleci.com/gh/bleeding182/minimalist-flashlight.svg?style=shield)](https://circleci.com/gh/bleeding182/minimalist-flashlight)

# Flashlight Widget for Android

Small and stable widget for android toggling the flash on/off.

[![Get it on Google Play][badge]][play]

[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=G35W5EF2LMG76&lc=US&item_name=David%20Medenjak&currency_code=EUR&bn=PP-DonationsBF%3Abtn_donate_LG.gif%3ANonHosted) 

## Introduction
The target was to make a small and stable widget for the android homescreen toggling the flashlight on and off, as most other flashlight apps have >1mb and / or ads and no activity is needed.

The code is commented and optimized to the best of my knowledge. This is a minimal example, trying to make it as simple as possible.

## Materials and Method
This project uses a *BroadcastReceiver* / *AppWidgetProvider* and a service to perform it's task. The state is persisted in a *SharedPreference*.

## Extras

At [Flashlight Widget][[play] you can get the app for free on Android.

[![Flattr this git repo][icon]][link]

  [badge]:https://developer.android.com/images/brand/en_app_rgb_wo_45.png
  [play]:https://play.google.com/store/apps/details?id=at.bleeding182.flashlight
  [icon]:http://api.flattr.com/button/flattr-badge-large.png
  [link]:https://flattr.com/submit/auto?user_id=bleeding182&url=https://github.com/bleeding182/minimalist-flashlight&title=Minimalist%20Flashlight&language=&tags=github&category=software
