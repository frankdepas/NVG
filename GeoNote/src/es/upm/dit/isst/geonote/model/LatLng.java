package es.upm.dit.isst.geonote.model;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class LatLng {

	private float latitude;
	private float longitude;
	
	public LatLng(float latitude, float longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public float getLatitude(){
		return latitude;
	}
	
	public float getLongitude(){
		return longitude;
	}
	
	public float distanceTo(float latitude, float longitude){
		return (float)distance(this.latitude, this.longitude, latitude, longitude);
	}
	
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        /*if (unit == 'K') {
          dist = dist * 1.609344;
        } else if (unit == 'N') {
          dist = dist * 0.8684;
        }*/
        return (dist);
    }
    

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }
	
	public float distanceTo(LatLng point){
		return 0.0f;
	}
	
	public static LatLng getPoint(JSONObject json){
		try{
			JSONObject point = json.getJSONObject("point");
			float latitude = (float)point.getDouble("latitude");
			float longitude = (float)point.getDouble("longitude");
			return new LatLng(latitude, longitude);
		}catch(JSONException e){
			return null;
		}
	}
	
}
