package com.davinci.wolf.about;

import com.davinci.wolf.about.AboutAdapter.AboutData;

/**
 * Created by aakash on 11/21/17.
 * List of all libraries and their respective authors
 */
class Dependencies {
	static AboutData[] getLibraries(){
		return new AboutData[] {
			new AboutData("<a href=\"https://developer.android.com/training/material/design-library.html\">Support Design Library</a>",
				"Android SDK"),
			new AboutData("<a href=\"https://developer.android.com/training/constraint-layout/index.html\">Constraint Layout</a>",
				"Android SDK"),
			new AboutData("<a href=\"https://developer.android.com/training/material/lists-cards.html\">Card View</a>",
				"Android SDK"),
			new AboutData("<a href=\"https://developers.google.com/maps/documentation/android-api/\">Google Play Services Maps</a>",
				"Android SDK"),
			new AboutData("<a href=\"https://developer.android.com/training/location/index.html\">Google Play Services Location</a>",
				"Android SDK"),
			new AboutData("<a href=\"https://developer.android.com/topic/libraries/architecture/index.html\">Architecture Components</a>",
				"Android SDK"),
			new AboutData("<a href=\"https://github.com/JakeWharton/timber\">Timber</a>",
				"<a href=\"https://github.com/JakeWharton\">Jake Wharton</a>"),
			new AboutData("<a href=\"https://github.com/google/dagger\">Dagger</a>",
				"<a href=\"https://github.com/google\">Google</a>"),
			new AboutData("<a href=\"https://github.com/ajalt/reprint\">Reprint</a>",
				"<a href=\"https://github.com/ajalt\">Aj Alt</a>"),
			new AboutData("<a href=\"https://github.com/tarek360/RichPath\">RichPath</a>",
				"<a href=\"https://github.com/tarek360\">Ahmed Tarek</a>"),
			new AboutData("<a href=\"https://github.com/akexorcist/Android-GoogleDirectionLibrary\">Android-GoogleDirectionLibrary</a>",
				"<a href=\"https://github.com/akexorcist/Android-GoogleDirectionLibrary\">Android-GoogleDirectionLibrary</a>"),
			new AboutData("<a href=\"https://github.com/patrickfav/Dali\">Dali</a>",
				"<a href=\"https://github.com/patrickfav\">Patrick Favre-Bulle</a>"),
			new AboutData("<a href=\"https://github.com/amlcurran/ShowcaseView\">ShowcaseView</a>",
				"<a href=\"https://github.com/amlcurran\">Alex Curran</a>")
		};
	}
}
