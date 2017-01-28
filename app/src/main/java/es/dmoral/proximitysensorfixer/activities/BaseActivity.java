package es.dmoral.proximitysensorfixer.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * This file is part of Proximity Sensor Fixer.
 *
 * Proximity Sensor Fixer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Proximity Sensor Fixer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proximity Sensor Fixer.  If not, see <http://www.gnu.org/licenses/>.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState, @LayoutRes int layoutId) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        ButterKnife.bind(this);
        setupViews();
        setListeners();
    }

    abstract void setupViews();

    abstract void setListeners();
}
