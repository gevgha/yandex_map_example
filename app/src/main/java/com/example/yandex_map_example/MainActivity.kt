package com.example.yandex_map_example

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError


class MainActivity : AppCompatActivity(),DrivingRouteListener {

    private var mapView: MapView? = null
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null

    private val ROUTE_START_LOCATION = Point(40.354582,45.1195619)
    private val ROUTE_END_LOCATION = Point(40.1535005,44.4185275)

    private val SCREEN_CENTER = Point(
        (ROUTE_START_LOCATION.latitude + ROUTE_END_LOCATION.latitude) / 2,
        (ROUTE_START_LOCATION.longitude + ROUTE_END_LOCATION.longitude) / 2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("001c3d14-5b55-44af-87f8-d5ba297a347c")
        MapKitFactory.initialize(this)
        DirectionsFactory.initialize(this)
        setContentView(R.layout.activity_main)
        mapView = findViewById<View>(R.id.mapview) as MapView

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = mapView!!.getMap().getMapObjects().addCollection()
//        mapView!!.map.move(
//            CameraPosition(Point(40.1535005,44.4185275), 11.0f, 0.0f, 0.0f),
//            Animation(Animation.Type.SMOOTH, 0f),
//            null
//        )
        submitRequest()
    }


    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onDrivingRoutes(routes: List<DrivingRoute>) {
        for (route in routes) {
            mapObjects!!.addPolyline(route.geometry)
        }
        mapView!!.getMap().move(
            CameraPosition(
                SCREEN_CENTER, 9f, 0f, 0f
            )
        )
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun submitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        val requestPoints = ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                ROUTE_START_LOCATION,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                ROUTE_END_LOCATION,
                RequestPointType.WAYPOINT,
                null
            )
        )
       val drivingSession =
            drivingRouter!!.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)
    }
}