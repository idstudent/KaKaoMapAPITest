package com.example.maptest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.IOException
import java.util.ArrayList


class MainActivity : AppCompatActivity(), MapView.POIItemEventListener {

    private var latitude : ArrayList<Double> = ArrayList()
    private var longtitude : ArrayList<Double> = ArrayList()

    private var locationManager: LocationManager? = null

    private var myLatitude: Double ?= null
    private var myLongtitude:Double ?= null

    private var invoiceMapItems = ArrayList<InvoiceMapItem>()

    private var clickLatitude: Double ?= null
    private var clickLongtitude: Double ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mapPoints: ArrayList<MapPoint>

        val mapView = MapView(this)

        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //사용자의 현재 위치
        val userLocation = getMyLocation()
        if (userLocation != null) {
            val latitude = userLocation?.latitude
            val longitude = userLocation?.longitude
            myLatitude = latitude
            myLongtitude = longitude
        }

        mapView.setDaumMapApiKey("앱키") // 카톡디벨로퍼에 등록한 앱키
        map_view.addView(mapView)

        // 주소 가져오기
        var dummyList : ArrayList<InvoiceItem> = getDummyData()

        // 해당주소의 위도, 경도 가져오기
        mapPoints = getLocation(dummyList)

        // 운송장 + 위도,경도 합체
        invoiceMapItems = makeInvoiceItems(dummyList, mapPoints)

        //내위치
        mapView.setMapCenterPoint(mapPoints[0], true)

        // 마커찍기
        setMarker(mapView, invoiceMapItems)

        // 카카오 네비로 감
        mapView.setPOIItemEventListener(this)
    }
    private fun getMyLocation(): Location? {
        var currentLocation: Location? = null
        val REQUEST_CODE_LOCATION = 2

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
            getMyLocation()
        } else {
            val locationProvider = LocationManager.GPS_PROVIDER
            currentLocation = locationManager?.getLastKnownLocation(locationProvider)
        }
        return currentLocation
    }
    private fun getDummyData(): ArrayList<InvoiceItem> {
        val testAddrList :ArrayList<InvoiceItem> = ArrayList()

        testAddrList.add(InvoiceItem("306728799823", "경기도 수원시 팔달구 교동 130-4", "홍길동1"))
        testAddrList.add(InvoiceItem("306728799845", "경기도 수원시 팔달구 교동 매산로 132", "홍길동2"))
        testAddrList.add(InvoiceItem("306728799834", "경기도 수원시 팔달구 교동 91-1", "홍길동3"))

        return testAddrList
    }

    private fun getLocation(invoiceItems: ArrayList<InvoiceItem>): ArrayList<MapPoint> {
        val mapList = ArrayList<MapPoint>()
        val addrList : ArrayList<Address> = ArrayList()

        try {
            val geoCoder = Geocoder(this)
            var tmpAddrList: List<Address>

            for (i in invoiceItems.indices) {
                tmpAddrList = geoCoder.getFromLocationName(invoiceItems[i].address, 1)
                for (j in tmpAddrList.indices) {
                    val lat = tmpAddrList[j].latitude
                    val lon = tmpAddrList[j].longitude
                    mapList.add(MapPoint.mapPointWithGeoCoord(lat, lon))
                }
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
    private fun makeInvoiceItems(dummyList: ArrayList<InvoiceItem>, mapPoints: ArrayList<MapPoint>): ArrayList<InvoiceMapItem> {
        val list = ArrayList<InvoiceMapItem>()
        var item: InvoiceMapItem
        for (i in dummyList.indices) {
            item = InvoiceMapItem()
            item.invoiceItem = dummyList[i]
            item.mapPoint = mapPoints[i]
            list.add(item)
        }
        return list
    }
    private fun setMarker(mapView: MapView, invoiceMapItems: ArrayList<InvoiceMapItem>) {
        var marker: MapPOIItem
        for (i in invoiceMapItems.indices) {
            marker = MapPOIItem()
            marker.itemName = invoiceMapItems[i].invoiceItem?.name
            marker.tag = 0
            marker.mapPoint = invoiceMapItems[i].mapPoint
            marker.markerType = MapPOIItem.MarkerType.BluePin
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
            mapView.addPOIItem(marker)
        }
    }
    private fun replaceUri(): String {
        var uri = "daummaps://route?sp=firstLatitude,firstLongtitude&ep=secondLatitude,secondLongittude&by=CAR"

        uri = uri.replace("firstLatitude", myLatitude.toString())
            .replace("firstLongtitude", myLongtitude.toString())
            .replace("secondLatitude", clickLatitude.toString())
            .replace("secondLongittude", clickLongtitude.toString())

        return uri
    }

    override fun onPOIItemSelected(mapView: MapView, mapPOIItem: MapPOIItem) {
        for (i in invoiceMapItems.indices) {
            if (invoiceMapItems[i].mapPoint === mapPOIItem.mapPoint) {

                ivNo.text = invoiceMapItems[i].invoiceItem?.ivNo
                address.text = invoiceMapItems[i].invoiceItem?.address
                name.text = invoiceMapItems[i].invoiceItem?.name
            }
        }

        for (i in invoiceMapItems.indices) {
            if (invoiceMapItems[i].mapPoint?.mapPointGeoCoord?.longitude === mapPOIItem.mapPoint.mapPointGeoCoord.longitude) {
                ivNo.text = invoiceMapItems[i].invoiceItem?.ivNo
                address.text = invoiceMapItems[i].invoiceItem?.address
                name.text = invoiceMapItems[i].invoiceItem?.name
            }
        }

        latitude.add(mapPOIItem.mapPoint.mapPointGeoCoord.latitude)
        longtitude.add(mapPOIItem.mapPoint.mapPointGeoCoord.longitude)

        clickLatitude = mapPOIItem.mapPoint.mapPointGeoCoord.latitude
        clickLongtitude = mapPOIItem.mapPoint.mapPointGeoCoord.longitude

        address.setOnClickListener {
            var uri : String = replaceUri()

            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
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
