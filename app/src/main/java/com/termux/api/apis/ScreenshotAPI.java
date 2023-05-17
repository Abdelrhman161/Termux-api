package com.termux.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

import com.termux.api.util.ResultReturner;
import com.termux.api.util.TermuxApiPermissionActivity;

public class ScreenshotAPI {

    private static final int REQUEST_CODE_CAPTURE_SCREENSHOT = 100;

    public static void onReceive(final TermuxApiReceiver apiReceiver, final Context context, final Intent intent) {
        ResultReturner.returnData(apiReceiver, intent, out -> {
            try {
                MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                TermuxApiPermissionActivity.startActivityForResult(context, mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_CAPTURE_SCREENSHOT, (resultCode, data) -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        String outputPath = intent.getStringExtra("path");
                        if(outputPath == null || outputPath.trim().isEmpty()) {
                            outputPath = "/sdcard/screenshot.png";
                        }

                        Intent screenshotIntent = new Intent(context, ScreenshotService.class);
                        screenshotIntent.putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode);
                        screenshotIntent.putExtra(ScreenshotService.EXTRA_DATA, data);
                        screenshotIntent.putExtra(ScreenshotService.EXTRA_OUTPUT_PATH, outputPath);
                        context.startService(screenshotIntent);

                        out.write(("Screenshot saved to: " + outputPath).getBytes());
                    } else {
                        out.write("Error: Failed to capture screenshot.".getBytes());
                    }
                });
            } catch (Exception e) {
                out.write(("Error: " + e.getMessage()).getBytes());
                e.printStackTrace();
            }
        });
    }
}
