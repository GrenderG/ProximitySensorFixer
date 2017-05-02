package es.dmoral.proximitysensorfixer.utils;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

public class ShellUtils {
    public static String runCommandWithOutput(@NonNull String command) {
        try {
            final Process process = Runtime.getRuntime().exec("su && " + command);
            final BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            return stringBuilder.toString();
        } catch (IOException ignored) {
        }

        return "";
    }
}
