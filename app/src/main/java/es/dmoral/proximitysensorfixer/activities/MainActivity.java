package es.dmoral.proximitysensorfixer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import es.dmoral.prefs.Prefs;
import es.dmoral.proximitysensorfixer.R;
import es.dmoral.proximitysensorfixer.services.ProximityFixerService;
import es.dmoral.proximitysensorfixer.utils.Constants;
import es.dmoral.proximitysensorfixer.utils.DeprecationUtils;
import es.dmoral.proximitysensorfixer.utils.RootUtils;
import es.dmoral.proximitysensorfixer.utils.ShellUtils;

/**
 * This file is part of Proximity Sensor Fix.
 *
 * Proximity Sensor Fix is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Proximity Sensor Fix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proximity Sensor Fix.  If not, see <http://www.gnu.org/licenses/>.
 */

public class MainActivity extends BaseActivity {
    @BindView(R.id.tv_log) TextView tvLog;
    @BindView(R.id.button_fix) Button buttonFix;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.UPDATE_FEEDBACK_LOG_ACTION);
        intentFilter.addAction(Constants.SERVICE_ENDED_ACTION);
        intentFilter.addAction(Constants.SERVICE_ERROR_ACTION);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.UPDATE_FEEDBACK_LOG_ACTION)) {
                    tvLog.append(intent.getExtras().getString(Constants.FEEDBACK_TEXT_EXTRA) + "\n");
                } else if (intent.getAction().equals(Constants.SERVICE_ERROR_ACTION)) {
                    tvLog.setTextColor(Color.RED);
                    tvLog.setText(getString(R.string.root_required_error));
                } else if (intent.getAction().equals(Constants.SERVICE_ENDED_ACTION)){
                    buttonFix.setAlpha(1f);
                    buttonFix.setEnabled(true);
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        showNotRootDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_force_recalibration:
                forceRecalibration();
                break;
            case R.id.action_donate:
                openDonateLink();
                break;
            case R.id.action_about:
                showAboutDialog();
                break;
        }

        return true;
    }

    @Override
    void setupViews() {
        tvLog.setMovementMethod(new ScrollingMovementMethod());

        tvLog.setText(getString(R.string.manufacturer_info, Build.MANUFACTURER, Build.MODEL, Build.BOARD, Build.BRAND, Build.DEVICE, Build.HARDWARE));
        if (Build.MANUFACTURER.contains(Constants.COMPATIBLE_MANUFACTURER) ||
                Build.BRAND.contains(Constants.COMPATIBLE_MANUFACTURER))
            tvLog.append(getString(R.string.compatible_disclaimer));
        else
            tvLog.append(getString(R.string.not_compatible_disclaimer));
        tvLog.append(getString(R.string.force_recalibration_disclaimer));
    }

    @Override
    void setListeners() {
        buttonFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLog.setTextColor(DeprecationUtils.getColor(MainActivity.this, R.color.logColor));
                tvLog.setText("\n");
                buttonFix.setAlpha(0.25f);
                buttonFix.setEnabled(false);
                startService(new Intent(MainActivity.this, ProximityFixerService.class));
            }
        });
    }

    private void forceRecalibration() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ShellUtils.runCommandWithOutput("cat /sys/devices/virtual/sensors/proximity_sensor/prox_cal").isEmpty()) {
                    sendBroadcast(new Intent(Constants.SERVICE_ERROR_ACTION));
                } else {
                    ShellUtils.runCommandWithOutput("echo 0 > /sys/devices/virtual/sensors/proximity_sensor/prox_cal");
                    ShellUtils.runCommandWithOutput("echo 1 > /sys/devices/virtual/sensors/proximity_sensor/prox_cal");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(R.string.recalibration_completed_msg)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.reboot, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ShellUtils.runCommandWithOutput("reboot");
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();
                        }
                    });
                }
            }
        }).start();
    }

    private void openDonateLink() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PAYPAL_DONATE_LINK)));
    }

    private void showAboutDialog() {
        final SpannableString spannableString = new SpannableString(getString(R.string.about_dialog_text));
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);

        final AlertDialog aboutDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.about_dialog_title)
                .setMessage(spannableString)
                .setPositiveButton(R.string.ok, null)
                .create();

        aboutDialog.show();

        ((TextView) aboutDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showNotRootDialog() {
        if (!RootUtils.isRooted()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.device_not_rooted)
                    .setMessage(R.string.device_not_rooted_msg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.continue_anyway, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showFirstBootDialog();
                        }
                    })
                    .setNegativeButton(R.string.exit_app, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        } else {
            showFirstBootDialog();
        }
    }

    private void showFirstBootDialog() {
        if (Prefs.with(this).readBoolean(Constants.PREFERENCES_FIRST_BOOT, true))
            new AlertDialog.Builder(this)
                    .setTitle(R.string.first_boot_dialog_title)
                    .setMessage(R.string.first_boot_dialog_text)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Prefs.with(MainActivity.this).writeBoolean(Constants.PREFERENCES_FIRST_BOOT, false);
                        }
                    })
                    .show();
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }
}