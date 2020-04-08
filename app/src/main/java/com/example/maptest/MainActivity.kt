package com.example.maptest

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.IOException
import java.util.ArrayList


class MainActivity : AppCompatActivity(), MapView.POIItemEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mapPoints : ArrayList<MapPoint>  = ArrayList()

        val mapView = MapView(this)
        mapView.setDaumMapApiKey("앱키 넣어야됨") // 카톡디벨로퍼에 등록한 앱키

        // 주소 가져오기
        var dummyList : ArrayList<String> = getDummyData()

        // 해당주소의 위도, 경도 가져오기
        mapPoints = getLocation(mapView, mapPoints, dummyList)

        // 마커찍기
        setMarker(mapView, mapPoints)

        // 주석풀면 카카오 네비로 감
        //        mapView.setPOIItemEventListener(this);

    }

    private fun getDummyData(): ArrayList<String> {
        val testAddrList : ArrayList<String> = ArrayList()
        testAddrList.add("경기도 수원시 팔달구 교동 130-4")
        testAddrList.add("경기도 수원시 팔달구 교동 매산로 132")
        testAddrList.add("경기도 수원시 팔달구 교동 91-1")
        testAddrList.add("경기도 수원시 팔달구 매교동")
        testAddrList.add("경기도 수원시 팔달구 교동 90-10")
        testAddrList.add("경기도 수원시 팔달구 교동 매산로 136 KR")
        testAddrList.add("경기도 수원시 팔달구 교동 90-7")
        testAddrList.add("경기도 수원시 팔달구 매교동 4-1")
        testAddrList.add("경기도 수원시 팔달구 교동 120-1")
        testAddrList.add("경기도 수원시 팔달구 교동 향교로 129")

        return testAddrList
    }

    private fun getLocation(mapView: MapView, mapPoint: ArrayList<MapPoint>, testAddrList: ArrayList<String>): ArrayList<MapPoint> {

        val addrList : ArrayList<Address> = ArrayList()

        try {
            val mCoder = Geocoder(this)

            for (i in testAddrList.indices) {
                val addr = mCoder.getFromLocationName(testAddrList[i], 5)
                addrList.addAll(addr)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for (i in addrList.indices) {
            val lating = addrList[i]

            val lat = lating.latitude
            val lon = lating.longitude

            mapPoint.add(MapPoint.mapPointWithGeoCoord(lat, lon))

            mapView.setMapCenterPoint(mapPoint[i], true)
        }
        map_view?.addView(mapView)

        return mapPoint
    }

    private fun setMarker(mapView: MapView, mapPoint: ArrayList<MapPoint>) {
        for (i in mapPoint.indices) {
            val marker = MapPOIItem()
            marker.itemName = "테스트$i"
            marker.tag = 0
            marker.mapPoint = mapPoint[i]
            marker.markerType = MapPOIItem.MarkerType.BluePin
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
            mapView.addPOIItem(marker)
        }
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
