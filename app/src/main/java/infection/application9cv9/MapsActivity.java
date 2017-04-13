package infection.application9cv9;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.face.Landmark;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import infection.application9cv9.Fragment.DialogNotificationFragment;
import infection.application9cv9.Modules.DirectionFinder;
import infection.application9cv9.Modules.DirectionFinderListener;
import infection.application9cv9.Modules.GPSTracker;
import infection.application9cv9.Modules.MapWrapperLayout;
import infection.application9cv9.Modules.OnInterInfoWindowTouchListener;
import infection.application9cv9.Modules.Route;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private float PlaceLat;
    private float PlaceLng;
    private LatLng eventPoint;
    private View contentView;
    private MapWrapperLayout mapWrapperLayout;
    private OnInterInfoWindowTouchListener lsClick;

    private List<Marker> StartPosMarkers = new ArrayList<>();
    private List<Marker> DestinationMarkers = new ArrayList<>();
    private List<Polyline> PolylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private OnInterInfoWindowTouchListener ghClick;
    private int map_type;
    private String eventName;
    private String eventAddress;
    private ArrayList<Landmark> landmarks;
    private String strStartPos;
    private String strDestination;
    private Integer personID;
    private ArrayList<Integer> Dist;
    private LatLng MyLoc;
    private int position;
    private Landmark pLandmark;

    private FloatingActionButton raiseAlert;
    private FloatingActionButton chooseDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_wrapper);

        initComponents();
        initListeners();
    }

    private void initComponents() {
        contentView = LayoutInflater.from(this).inflate(R.layout.layout_maker_content, null);
        raiseAlert = (FloatingActionButton) findViewById(R.id.fab_raise_alert);
        chooseDestination = (FloatingActionButton) findViewById(R.id.fab_chooseDest);
        raiseAlert.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        chooseDestination.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        chooseDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                DialogNotificationFragment chooseDestDialog = new DialogNotificationFragment();
                chooseDestDialog.show(transaction, "Choose Destination");
                Log.d("abcd", chooseDestDialog.getDest());
            }
        });
    }

    private void initListeners() {
//        btnFindPath.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendFindPathRequest();
//            }
//        });
//
//        // Start of click button Invite
//        lsClick = new OnInterInfoWindowTouchListener(btnInvite) {
//            @Override
//            protected void onClickConfirmed(View v, Marker marker) {
//                int pos = Integer.parseInt(marker.getTitle());
//            }
//        };
//        btnInvite.setOnTouchListener(lsClick);
        // End of click button Invite

//        //Start of click button GetHere
//        ghClick = new OnInterInfoWindowTouchListener(btnGetHere) {
//            @Override
//            protected void onClickConfirmed(View v, Marker marker) {
//                if (mMap.getMyLocation() != null)
//                    MyLoc = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
//                etStartPos.setText(MyLoc.latitude + " " + MyLoc.longitude);
//                etDestination.setText(marker.getPosition().latitude + " " + marker.getPosition().longitude);
//                sendFindPathRequest();
//            }
//        };
//        btnGetHere.setOnTouchListener(ghClick);
        //End of click button GetHere

    }

    public void sendFindPathRequest(String etDestination) {
        GPSTracker gps = new GPSTracker(this);
        strStartPos = gps.getLatitude() + ", " + gps.getLongitude();
        strDestination = etDestination;


        if (strStartPos.isEmpty()) {
            Toast.makeText(this, "Please enter the starting point", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strDestination.isEmpty()) {
            Toast.makeText(this, "Please enter the destination", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, strStartPos, strDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapWrapperLayout.init(mMap, this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        //Handle Popup Info window
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                lsClick.setMarker(marker);
                ghClick.setMarker(marker);

                Integer pos = Integer.parseInt(marker.getTitle());
                if (pos == -1) {

                } else {

                }

                mapWrapperLayout.setMarkerWithInfoWindow(marker, contentView);
                return contentView;
            }
        });
    }


    private void MarkPlace(Integer id) {
        MarkerOptions mo2 = new MarkerOptions()
                .position(eventPoint)
                .title(id.toString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot));

        MarkerOptions mo1 = new MarkerOptions()
                .position(eventPoint)
                .title(id.toString());
        if (id == -1)
            mMap.addMarker(mo1);
        else mMap.addMarker(mo2);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(eventPoint)
                .zoom(18)
                .bearing(90)
                .tilt(30)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait", "Finding Direction", true);

        if (StartPosMarkers != null) {
            for (Marker marker : StartPosMarkers)
                marker.remove();
        }

        if (DestinationMarkers != null) {
            for (Marker marker : DestinationMarkers)
                marker.remove();
        }

        if (PolylinePaths != null) {
            for (Polyline polyline : PolylinePaths)
                polyline.remove();
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        Marker StartPosMarker = null, DestinationMarkers;
        PolylinePaths = new ArrayList<>();
        ArrayList<LatLng> listTrip = new ArrayList<>();

        for (final Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            StartPosMarker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start))
                    .title(route.startAddress)
                    .position(route.startLocation));

            DestinationMarkers = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_des))
                    .title(route.endAddress)
                    .position(route.endLocation));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.RED)
                    .width(15);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
                listTrip.add(route.points.get(i));
            }

            PolylinePaths.add(mMap.addPolyline(polylineOptions));
        }
        MarkerAnimation markerAnimation = new MarkerAnimation();
        markerAnimation.animateLine(listTrip, mMap, StartPosMarker, this);
    }

    public class MarkerAnimation {
        ArrayList<LatLng> _trips = new ArrayList<>();
        Marker _marker;
        LatLngInterpolator _latLngInterpolator = new LatLngInterpolator.Spherical();

        public void animateLine(ArrayList<LatLng> Trips, GoogleMap map, Marker marker, Context current) {
            _trips.addAll(Trips);
            _marker = marker;

            animateMarker();
        }

        public void animateMarker() {
            TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
                @Override
                public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                    return _latLngInterpolator.interpolate(fraction, startValue, endValue);
                }
            };
            Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");

            ObjectAnimator animator = ObjectAnimator.ofObject(_marker, property, typeEvaluator, _trips.get(0));

            //ObjectAnimator animator = ObjectAnimator.o(view, "alpha", 0.0f);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    //  animDrawable.stop();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //  animDrawable.stop();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    //  animDrawable.stop();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //  animDrawable.stop();
                    if (_trips.size() > 1) {
                        _trips.remove(0);
                        animateMarker();
                    }
                }
            });

            animator.setDuration(300);
            animator.start();
        }
    }
}