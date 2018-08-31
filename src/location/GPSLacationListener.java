package location;

import android.location.Location;
import android.os.Bundle;

public interface GPSLacationListener {
	public void onLocationChanged(Location location);
	public void onStatusChanged(String provider, int status, Bundle extras);
}
