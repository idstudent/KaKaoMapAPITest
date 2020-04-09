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

    private var latitude : ArrayList<Double> = ArrayList()
    private var longtitude : ArrayList<Double> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mapPoints: ArrayList<MapPoint>

        val mapView = MapView(this)

        mapView.setDaumMapApiKey("앱키") // 카톡디벨로퍼에 등록한 앱키
        map_view.addView(mapView)

        // 주소 가져오기
        var dummyList : ArrayList<String> = getDummyData()

        // 해당주소의 위도, 경도 가져오기
        mapPoints = getLocation(dummyList)

        //내위치
        mapView.setMapCenterPoint(mapPoints[0], true)

        // 마커찍기
        setMarker(mapView, mapPoints)

        // 주석풀면 카카오 네비로 감
        mapView.setPOIItemEventListener(this)
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

    private fun getLocation(testAddrList: ArrayList<String>): ArrayList<MapPoint> {
        val mapList = ArrayList<MapPoint>()
        val addrList : ArrayList<Address> = ArrayList()

        try {
            val mCoder = Geocoder(this)
            var tmpAddrList: List<Address>
            for (i in testAddrList.indices) {
                tmpAddrList = mCoder.getFromLocationName(testAddrList[i], 5)
                addrList.addAll(tmpAddrList)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for (i in addrList.indices) {

            val lat = addrList[i].latitude
            val lon = addrList[i].longitude

            mapList.add(MapPoint.mapPointWithGeoCoord(lat, lon))

        }
        return mapList
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
    private fun replaceUri(): String {
        var uri = "daummaps://route?sp=firstLatitude,firstLongtitude&ep=secondLatitude,secondLongittude&by=CAR"

        uri = uri.replace("firstLatitude", latitude[0].toString())
            .replace("firstLongtitude", longtitude[0].toString())
            .replace("secondLatitude", latitude[1].toString())
            .replace("secondLongittude", longtitude[1].toString())

        return uri
    }

    override fun onPOIItemSelected(mapView: MapView, mapPOIItem: MapPOIItem) {
        latitude.add(mapPOIItem.mapPoint.mapPointGeoCoord.latitude)
        longtitude.add(mapPOIItem.mapPoint.mapPointGeoCoord.longitude)

        if (longtitude.size == 2) {

            val uri = replaceUri()

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)

            latitude.clear()
            longtitude.clear()
        }
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
