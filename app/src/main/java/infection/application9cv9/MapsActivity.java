package infection.application9cv9;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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


import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import infection.application9cv9.Fragment.DialogPopUpFragment;
import infection.application9cv9.Modules.DirectionFinder;
import infection.application9cv9.Modules.DirectionFinderListener;
import infection.application9cv9.Modules.GPSTracker;
import infection.application9cv9.Modules.MapWrapperLayout;
import infection.application9cv9.Modules.OnInterInfoWindowTouchListener;
import infection.application9cv9.Modules.Route;
import infection.application9cv9.ServerWidget.JsonHelper;
import infection.application9cv9.ServerWidget.LoadRoadInfo;
import infection.application9cv9.ServerWidget.RequestToServer;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;


public class MapsActivity extends FragmentActivity implements RequestToServer.RequestResult, OnMapReadyCallback, DirectionFinderListener, LoadRoadInfo.OnFinishLoadRoadInfo, DialogPopUpFragment.OnCompleteDialog{

    private GoogleMap mMap;
    private float PlaceLat;
    private float PlaceLng;
    private LatLng eventPoint;
    private View contentView;
    private MapWrapperLayout mapWrapperLayout;
    private OnInterInfoWindowTouchListener lsClick;

    private ImageView ivInstruction, ivPrevInstruction, ivNextInstruction;
    private TextView tvInstruction;
    private LinearLayout llInstruction;

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

    DialogPopUpFragment dialogPopUpFragment;
    int posInstruction;
    private Route mRoute;

    public static String destination;
    private PolylineOptions mPolylineOps;
    private Polyline mPolyline;

    LoadRoadInfo loadRoadInfo;
    RequestToServer requestToServer;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
//        loadRoadInfo = new LoadRoadInfo(this);
//        loadRoadInfo.delegate = this;
//        loadRoadInfo.execute("http://192.168.1.60:5000/xvideos");
//        startService(new Intent(this, NotificationService.class));
//        JsonHelper helper = new JsonHelper();
//        try {
//            Log.d("TuTuTu", helper.writeQuery("111.111", "112.222", "0.0", "5.0"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        requestToServer = new RequestToServer();
        requestToServer.delegate = this;
        try {
            requestToServer.execute(new JsonHelper().writeQuery("10.75201684447029", "106.65901912611416", "10.756377781053402", "106.68518281777517"), "http://192.168.1.60:5000/xhamster");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initComponents() {
        contentView = LayoutInflater.from(this).inflate(R.layout.layout_maker_content, null);
        raiseAlert = (FloatingActionButton) findViewById(R.id.fab_raise_alert);
        chooseDestination = (FloatingActionButton) findViewById(R.id.fab_chooseDest);
        raiseAlert.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        chooseDestination.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        raiseAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you stuck?")
                        .setContentText("Are you in a traffic jam and wanna find another way?")
                        .setCancelText("NO")
                        .setConfirmText("YES")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        chooseDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                dialogPopUpFragment = new DialogPopUpFragment();
                dialogPopUpFragment.show(transaction, "Choose Dest");
            }
        });
        ivInstruction = (ImageView) findViewById(R.id.iv_ins_direction);
        tvInstruction  = (TextView) findViewById(R.id.tv_ins_text);
        llInstruction = (LinearLayout) findViewById(R.id.ll_instruction);
        ivPrevInstruction = (ImageView) findViewById(R.id.iv_ins_prev);
        ivNextInstruction = (ImageView) findViewById(R.id.iv_ins_next);
    }

    private void initListeners() {


    }

    public void sendFindPathRequest(String etDestination) {
        GPSTracker gps = new GPSTracker(this);
        strStartPos = gps.getLatitude() + ", " + gps.getLongitude();
        strDestination = etDestination;

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

        if (mMap!=null) {
            RequestToServer rts = new RequestToServer();
            rts.delegate = this;
            try {
                Log.d("Request", "sent");
                rts.execute(new JsonHelper().writeQuery("10.766017", "106.67499", "10.763360", "106.687014"), "http://192.168.1.107:5000/xhamster");
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Log.d("loading", "loaded");
//            loadRoadInfo = new LoadRoadInfo(this);
//            loadRoadInfo.delegate = this;
//            loadRoadInfo.execute("http://192.168.1.60:5000/xvideos");
        }

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

        for (Route route : routes) {
            mRoute = route;
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
                    .color(Color.BLUE)
                    .width(15);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
                listTrip.add(route.points.get(i));
            }

            PolylinePaths.add(mMap.addPolyline(polylineOptions));
        }
        MarkerAnimation markerAnimation = new MarkerAnimation();
        markerAnimation.animateLine(listTrip, mMap, StartPosMarker, this);

        posInstruction = 0;
        llInstruction.setVisibility(View.VISIBLE);
        showInstruction();

        ivNextInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posInstruction < mRoute.steps.size() - 1)
                    posInstruction += 1;
                showInstruction();
            }
        });

        ivPrevInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posInstruction > 0)
                    posInstruction-=1;
                showInstruction();
            }
        });
    }

    public void showInstruction(){
        String strInstruction = html2text(mRoute.steps.get(posInstruction).htmlInstructions);
        tvInstruction.setText(strInstruction);

        if (mRoute.steps.get(posInstruction).maneuver!=null) {
            if (mRoute.steps.get(posInstruction).maneuver.equals("turn-left"))
                ivInstruction.setImageResource(R.drawable.go_ahead);
            else if (mRoute.steps.get(posInstruction).maneuver.equals("turn-right"))
                ivInstruction.setImageResource(R.drawable.turn_right);
            else ivInstruction.setImageResource(R.drawable.go_ahead);
        } else ivInstruction.setImageResource(R.drawable.go_ahead);

        if (mPolyline != null)
            mPolyline.remove();
        mPolylineOps = new PolylineOptions()
                .geodesic(true)
                .color(Color.MAGENTA)
                .width(15);
        for (int i=0; i<mRoute.steps.get(posInstruction).points.size()-1; i++)
            mPolylineOps.add(mRoute.steps.get(posInstruction).points.get(i));
        mPolyline = mMap.addPolyline(mPolylineOps);
    }

    public String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    @Override
    public void onComplete(String result) {
        sendFindPathRequest(result);
    }

    @Override
    public void processFinish(ArrayList<Route> listRoadInfo) {
        GPSTracker gps = new GPSTracker(this);
        LatLng myLoc = new LatLng(gps.getLatitude(), gps.getLongitude());
        for (Route route : listRoadInfo) {
            double dist = computeDistanceBetween(myLoc, route.points.get(0));
            if (dist <= 1000) {
                PolylineOptions polylineOps = new PolylineOptions()
                        .geodesic(true)
                        .color(Color.RED)
                        .width(15)
                        .add(route.points.get(0))
                        .add(route.points.get(1));
                mMap.addPolyline(polylineOps);
            }
        }
    }

    @Override
    public void processFinish(String result) {

    }

//    @Override
//    public void processFinish(String result) {
//
//    }


    public class MarkerAnimation {
        ArrayList<LatLng> _trips = new ArrayList<>();
        Marker _marker;
        LatLngInterpolator _latLngInterpolator = new LatLngInterpolator.Spherical();

        public void animateLine(ArrayList<LatLng> Trips, GoogleMap map, Marker marker, Context current) {
            _trips.addAll(Trips);
            _marker = marker;
            int dist=1;
            if (Trips.size()>1) {
                dist = (int) computeDistanceBetween(Trips.get(0), Trips.get(1));
            }
            animateMarker((dist/22)*1000);
        }

        public void animateMarker(int speed) {
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
                        int dist=3000;
                        if (_trips.size()>1) {
                            dist = (int) ((int) 1000*computeDistanceBetween(_trips.get(0), _trips.get(1)));
                        }
                        animateMarker((dist / 22));
                    }
                }
            });

            animator.setDuration(speed);
            animator.start();
        }
    }
}