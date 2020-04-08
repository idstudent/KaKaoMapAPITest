package com.example.maptest

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.IOException
import java.util.ArrayList


class MainActivity : AppCompatActivity(), MapView.POIItemEventListener {
    private var mCoder: Geocoder ?= null

    private var addr: List<Address> = ArrayList()
    private val addrList : ArrayList<Address> = ArrayList()
    private val testAddrList : ArrayList<String> = ArrayList()

    private var lating: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapPoint = arrayOfNulls<MapPoint>(2)

        val mapView = MapView(this)
        mapView.setDaumMapApiKey("앱키 넣어야됨") // 카톡디벨로퍼에 등록한 앱키

        val mapViewContainer = findViewById<ViewGroup>(R.id.map_view)

        mCoder = Geocoder(this)

        testAddrList.add("경기도 수원시 팔달구 교동 130-4")
        testAddrList.add("경기도 수원시 팔달구 교동 매산로 132")

        try {
            for (i in testAddrList.indices) {
                addr = mCoder?.getFromLocationName(testAddrList.get(i), 5)!!
                addrList.addAll(addr)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for (i in addrList.indices) {
            lating = addrList[i]

            val lat = lating!!.latitude
            val lon = lating!!.longitude

            mapPoint[i] = MapPoint.mapPointWithGeoCoord(lat, lon)

            mapView.setMapCenterPoint(mapPoint[i], true)
        }
        mapViewContainer.addView(mapView)

        val marker1 = MapPOIItem()
        marker1.itemName = "테스트1"
        marker1.tag = 0
        marker1.mapPoint = mapPoint[0]
        // 기본으로 제공하는 BluePin 마커 모양.
        marker1.markerType = MapPOIItem.MarkerType.BluePin
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker1.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker1)

        val marker2 = MapPOIItem()
        marker2.itemName = "테스트2"
        marker2.tag = 0
        marker2.mapPoint = mapPoint[1]
        // 기본으로 제공하는 BluePin 마커 모양.
        marker2.markerType = MapPOIItem.MarkerType.BluePin
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker2.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker2)

        // 주석풀면 카카오 네비로 감
        //        mapView.setPOIItemEventListener(this);

    }

    override fun onPOIItemSelected(mapView: MapView, mapPOIItem: MapPOIItem) {
        Toast.makeText(applicationContext, "클릭 " + mapPOIItem.getItemName(), Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("daummaps://route?sp=37.484246,126.975832&ep=37.483911,126.974412&by=CAR") // 위도, 경도 좌표 (구글지도)
        )
        startActivity(intent)
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView, mapPOIItem: MapPOIItem) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(
        mapView: MapView,
        mapPOIItem: MapPOIItem,
        calloutBalloonButtonType: MapPOIItem.CalloutBalloonButtonType
    ) {

    }

    override fun onDraggablePOIItemMoved(mapView: MapView, mapPOIItem: MapPOIItem, mapPoint: MapPoint) {

    }
}
