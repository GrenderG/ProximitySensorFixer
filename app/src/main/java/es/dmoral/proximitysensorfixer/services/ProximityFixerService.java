package es.dmoral.proximitysensorfixer.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import es.dmoral.proximitysensorfixer.R;
import es.dmoral.proximitysensorfixer.utils.Constants;
import es.dmoral.proximitysensorfixer.utils.RootUtils;
import es.dmoral.proximitysensorfixer.utils.ShellUtils;

/**
 * This file is part of Proximity Sensor Fixer.
 * <p>
 * Proximity Sensor Fixer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 * <p>
 * Proximity Sensor Fixer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Proximity Sensor Fixer.  If not, see <http://www.gnu.org/licenses/>.
 */

public class ProximityFixerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executeFixAction();
        return START_NOT_STICKY;
    }

    private void executeFixAction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* SystemClocks are set in order to give the log a nice look and to give time
                       to the user to read the output. */
                    sendUpdateLogBroadcast(getString(R.string.fix_text_1));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_2));
                    SystemClock.sleep(1250);
                    final int offsetValue = Integer.parseInt(ShellUtils
                            .runCommandWithOutput("cat /sys/devices/virtual/sensors/proximity_sensor/prox_cal").split(",")[0]);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_3, offsetValue));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_4));
                    SystemClock.sleep(1250);
                    final int normalValue = Integer.parseInt(ShellUtils
                            .runCommandWithOutput("cat /sys/devices/virtual/sensors/proximity_sensor/state"));
                    sendUpdateLogBroadcast(getString(R.string.fix_text_5, normalValue));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_6));
                    SystemClock.sleep(750);
                    final int valueSum = normalValue + offsetValue;
                    sendUpdateLogBroadcast(getString(R.string.fix_text_7, valueSum));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_8));
                    SystemClock.sleep(500);
                    final String hexValue = Integer.toHexString(valueSum);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_9, hexValue));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_10, hexValue));
                    SystemClock.sleep(1500);
                    ShellUtils.runCommandWithOutput("echo -en $'" + hexValue + "' > /efs/prox_cal");
                    sendUpdateLogBroadcast(getString(R.string.fix_text_11));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_12));
                    SystemClock.sleep(1250);
                    ShellUtils.runCommandWithOutput("chown system:system /efs/prox_cal");
                    sendUpdateLogBroadcast(getString(R.string.fix_text_13));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_14));
                    SystemClock.sleep(1250);
                    ShellUtils.runCommandWithOutput("chmod 644 /efs/prox_cal");
                    sendUpdateLogBroadcast(getString(R.string.fix_text_15));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_16));
                    SystemClock.sleep(1750);
                    ShellUtils.runCommandWithOutput("sync");
                    sendUpdateLogBroadcast(getString(R.string.fix_text_17));
                    SystemClock.sleep(500);
                    sendUpdateLogBroadcast(getString(R.string.fix_text_18));
                } catch (Exception genericException) {
                    if (genericException instanceof NumberFormatException && RootUtils.isRooted()) {
                        // Try another location
                        sendUpdateLogBroadcast(getString(R.string.fix_another_option));
                        SystemClock.sleep(1250);
                        if (!ShellUtils.runCommandWithOutput("cat /sys/class/sensors/proximity_sensor/prox_cal").isEmpty()) {
                            ShellUtils.runCommandWithOutput("echo 0 > /sys/class/sensors/proximity_sensor/prox_cal");
                            ShellUtils.runCommandWithOutput("echo 1 > /sys/class/sensors/proximity_sensor/prox_cal");
                            SystemClock.sleep(750);
                            sendUpdateLogBroadcast(getString(R.string.fix_text_18));
                        } else {
                            sendActionBroadcast(Constants.SERVICE_ERROR_ACTION);
                        }
                    } else {
                        sendActionBroadcast(Constants.SERVICE_ERROR_ACTION);
                    }
                } finally {
                    sendUpdateLogBroadcast(getString(R.string.donate_msg));
                    sendActionBroadcast(Constants.SERVICE_ENDED_ACTION);
                    stopSelf();
                }
            }
        }).start();
    }

    private void sendActionBroadcast(@NonNull String action) {
        sendBroadcast(new Intent(action));
    }

    private void sendUpdateLogBroadcast(@NonNull String feedback) {
        final Intent updateIntent = new Intent(Constants.UPDATE_FEEDBACK_LOG_ACTION);
        updateIntent.putExtra(Constants.FEEDBACK_TEXT_EXTRA, feedback);
        sendBroadcast(updateIntent);
    }
}
