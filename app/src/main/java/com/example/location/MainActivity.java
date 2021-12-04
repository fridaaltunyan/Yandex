
package com.example.location;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.geometry.SubpolylineHelper;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.Callback;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.MasstransitOptions;
import com.yandex.mapkit.transport.masstransit.MasstransitRouter;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Section;
import com.yandex.mapkit.transport.masstransit.SectionMetadata;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.mapkit.transport.masstransit.Transport;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {
    private MapView mapview;
    private UserLocationLayer userLocationLayer;
    private SearchManager searchManager;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private Button button;
    private Button send_btn;
    private Button direction_btn;
    private SearchView searchView;
    private List<GeoObjectCollection.Item> mapObjects;
    private MasstransitRouter mtRouter;
    private Location myLocation;
    private MapKit mapKit;
    private MapObjectCollection map_objects;
    private GetPlaces getPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("4fcea416-4528-4fa0-a217-b3b16f4c61ab");
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        TransportFactory.initialize(this);
        setContentView(R.layout.activity_main);
        init();
        getLocationByClicking();
        userLocationLayer = mapKit.createUserLocationLayer(mapview.getMapWindow());
        sendNotification();
    }

    private void submitQuery(String query, Session.SearchListener searchListener) {
        Session searchSession = searchManager.submit(query,
                VisibleRegionUtils.toPolygon(mapview.getMap().getVisibleRegion()),
                new SearchOptions(),
                searchListener);

    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();

    }

    public void init() {
        mapview = (MapView) findViewById(R.id.mapview);
        button = findViewById(R.id.button);
        send_btn = findViewById(R.id.send_not);
        direction_btn = findViewById(R.id.btn_dir);
        searchView = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayout = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayout);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

    }

    public void getLocationByClicking() {
        mapKit = MapKitFactory.getInstance();
        button.setOnClickListener(v ->
                mapKit.createLocationManager().requestSingleUpdate(new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                Log.e("TagCheck", "LocationUpdated " + location.getPosition().getLongitude());
                Log.e("TagCheck", "LocationUpdated " + location.getPosition().getLatitude());
                mapview.getMap().move(
                        new CameraPosition(location.getPosition(), 14.0f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);
                userLocationLayer.setVisible(true);
                userLocationLayer.setHeadingEnabled(true);
                userLocationLayer.setObjectListener(MainActivity.this);
            myLocation = location;
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {

            }
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerView.setVisibility(View.VISIBLE);
                submitQuery(newText, new Session.SearchListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSearchResponse(@NonNull Response response) {
                        mapObjects = response.getCollection().getChildren();
                        adapter = new Adapter(MainActivity.this, mapObjects);

                        adapter.setOnForPointListener(point -> {
                            MapObjectCollection map_objects = mapview.getMap().getMapObjects();
                            if (point != null) {
                                map_objects.addPlacemark(
                                        point,
                                        ImageProvider.fromResource(MainActivity.this, R.drawable.search_result));
                                mapview.getMap().move(
                                        new CameraPosition(point, 14.0f, 0.0f, 0.0f),
                                        new Animation(Animation.Type.SMOOTH, 1),
                                        null);
                            }
                            direction_btn.setOnClickListener(v -> {
                                getDirection(point);
                            });

                            recyclerView.setVisibility(View.INVISIBLE);
                        });

                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                LinearLayoutManager.VERTICAL);
                        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        recyclerView.setAdapter(adapter);


                    }

                    @Override
                    public void onSearchError(@NonNull Error error) {
                        Toast.makeText(MainActivity.this, "onSearchError", Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    public void getDirection(Point point) {
        map_objects = mapview.getMap().getMapObjects().addCollection();
        MasstransitOptions options = new MasstransitOptions(
                new ArrayList<String>(),
                new ArrayList<String>(),
                new TimeOptions());
        List<RequestPoint> points = new ArrayList<RequestPoint>();
        points.add(new RequestPoint(myLocation.getPosition(), RequestPointType.WAYPOINT, null));
        points.add(new RequestPoint(point, RequestPointType.WAYPOINT, null));
        mtRouter = TransportFactory.getInstance().createMasstransitRouter();
        com.yandex.mapkit.transport.masstransit.Session.RouteListener listener =
                new com.yandex.mapkit.transport.masstransit.Session.RouteListener() {
                    @Override
                    public void onMasstransitRoutes(@NonNull List<Route> routes) {
                        int count = 0;
                        if (routes.size() > 0) {
                            for (Section section : routes.get(0).getSections()) {
                                if(section!=null){
                                    count++;

                                    Log.e("hey", String.valueOf(count));
                                }
                                drawSection(
                                        section.getMetadata().getData(),
                                        SubpolylineHelper.subpolyline(
                                                routes.get(0).getGeometry(), section.getGeometry()));
                            }

                        }

                    }

                    @Override
                    public void onMasstransitRoutesError(@NonNull Error error) {
                        Toast.makeText(MainActivity.this, "error in getdirection", Toast.LENGTH_SHORT).show();
                    }

                };
        mtRouter.requestRoutes(points, options, listener);
    }

    private void drawSection(SectionMetadata.SectionData data,
                             Polyline geometry) {
        PolylineMapObject polylineMapObject = map_objects.addPolyline(geometry);
        polylineMapObject.setStrokeColor(R.color.black);
        if (data.getTransports() != null) {

            for (Transport transport : data.getTransports()) {

                if (transport.getLine().getStyle() != null) {
                    polylineMapObject.setStrokeColor(
                            transport.getLine().getStyle().getColor() | R.color.black
                    );
                    return;
                }


            }
        }

    }
    public void sendNotification(){
        send_btn.setOnClickListener(v -> {
            //String address =  searchView.getQuery().toString();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);
            mBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_active_24);
            mBuilder.setContentTitle("Notification Alert, Click Me!");
            mBuilder.setContentText("Hi, This is Android Notification Detail!");
            Intent resultIntent = new Intent(MainActivity.this, NotificationActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra("message", "okay");
            resultIntent.putExtra("message2","baby");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
            stackBuilder.addParentStack(NotificationActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                String channelId = "Your_channel_id";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }
            notificationManager.notify(0, mBuilder.build());
        });

    }

}

