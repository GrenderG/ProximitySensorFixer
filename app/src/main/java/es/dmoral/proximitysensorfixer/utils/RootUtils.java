package es.dmoral.proximitysensorfixer.utils;

import android.support.annotation.NonNull;

import java.io.File;

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

public class RootUtils {
    // Based on http://stackoverflow.com/a/19289543/4208583
    public static boolean isRooted() {
        return findBinary("su");
    }

    private static boolean findBinary(@NonNull String binaryName) {
        final String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
        for (String where : places) {
            if (new File(where + binaryName).exists()) {
                return true;
            }
        }
        return false;
    }
}
