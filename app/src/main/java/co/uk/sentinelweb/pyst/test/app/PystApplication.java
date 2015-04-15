package co.uk.sentinelweb.pyst.test.app;

import android.app.Application;

import sentinelweb.uk.co.pyst.test.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by robert on 15/04/2015.
 */
public class PystApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Light.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
