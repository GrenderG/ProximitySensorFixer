package es.dmoral.proximitysensorfixer;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

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

public class ProximitySensorFixerApplication extends Application {
    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
