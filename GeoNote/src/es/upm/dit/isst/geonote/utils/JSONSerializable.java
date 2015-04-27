package es.upm.dit.isst.geonote.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public abstract class JSONSerializable {
	
	@SuppressWarnings("unchecked")
	public JSONObject serializeToJSONObject(){
		JSONObject json = new JSONObject();
		Field[] fields = getClass().getDeclaredFields();
		for(Field field : fields){
			if(!shouldBeSerialized(field)) continue;
			field.setAccessible(true);
			try{
				if(JSONSerializable.class.isAssignableFrom(field.getType())){
					JSONSerializable serializable = (JSONSerializable)field.get(this);
					json.put(field.getName(), serializable.serializeToJSONObject());
				}else if(List.class == field.getType()){
					json.put(field.getName(), serializeList((List<Object>)field.get(this)));
				}else if(Set.class == field.getType()){
					json.put(field.getName(), serializeSet((Set<Object>)field.get(this)));
				}else if(Date.class == field.getType()){
					json.put(field.getName(), ((Date)field.get(this)).getTime());
				}else json.put(field.getName(), field.get(this));
			}catch(JSONException | IllegalArgumentException | IllegalAccessException e){}
		}
		return json;
	}

	protected void deserializeJSONObject(JSONObject json) throws JSONException {
		Field[] fields = getClass().getDeclaredFields();
		for(Field field : fields){
			if(!shouldBeDeserialized(field)) continue;
			field.setAccessible(true);
			try{
				if(JSONSerializable.class.isAssignableFrom(field.getType())){
					JSONSerializable serializable = (JSONSerializable)field.getType()
							.getDeclaredConstructor(JSONObject.class)
							.newInstance(json.getJSONObject(field.getName()));
					field.set(this, serializable);
				}else if(List.class == field.getType()){
					JSONArray array = json.getJSONArray(field.getName());
					List<Object> list = deserializeList(array, 
							((ParameterizedType)field.getGenericType())
							.getActualTypeArguments()[0]);
					field.set(this, list);
				}else if(Set.class == field.getType()){
					JSONArray array = json.getJSONArray(field.getName());
					Set<Object> set = deserializeSet(array, 
							((ParameterizedType)field.getGenericType())
							.getActualTypeArguments()[0]);
					field.set(this, set);
				}else if(Date.class == field.getType()){
					field.set(this, new Date(json.getLong(field.getName())));
				}else if(field.getType().isPrimitive()){
					if(field.getType() == float.class)
						field.set(this, (float)json.getDouble(field.getName()));
					else if(field.getType() == double.class)
						field.set(this, json.getDouble(field.getName()));
					else if(field.getType() == boolean.class)
						field.set(this, json.getBoolean(field.getName()));
					else if(field.getType() == short.class)
						field.set(this, (short)json.getInt(field.getName()));
					else if(field.getType() == char.class)
						field.set(this, (char)json.getInt(field.getName()));
					else if(field.getType() == byte.class)
						field.set(this, (byte)json.getInt(field.getName()));
					else if(field.getType() == int.class)
						field.set(this, json.getInt(field.getName()));
					else if(field.getType() == long.class)
						field.set(this, json.getLong(field.getName()));
					else field.set(this, json.get(field.getName()));
				}else{
					if(field.getName().equals("id")){
						try{
							field.set(this, json.get(field.getName()));
						}catch(JSONException e){
							field.set(this, -1);
						}
					}else field.set(this, json.get(field.getName()));
				}
			}catch(IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException
					| SecurityException  e){}
		}
	}
	
	private JSONArray serializeSet(Set<Object> set){
		JSONArray array = new JSONArray();
		if(set==null) return array;
		Iterator<Object> i = set.iterator();
		while(i.hasNext()){
			Object obj = i.next();
			if(obj != null){
				if(JSONSerializable.class.isAssignableFrom(obj.getClass())){
					JSONSerializable serializable = (JSONSerializable)obj;
					array.put(serializable.serializeToJSONObject());					
				}else array.put(obj);
			}
		}
		return array;
	}
	
	private Set<Object> deserializeSet(JSONArray array, Type type) throws JSONException{
		Set<Object> set = new HashSet<Object>();
		for(int i = 0; i < array.length(); i++){
			if(JSONSerializable.class.isAssignableFrom(type.getClass())){
				try {
					JSONObject json = array.getJSONObject(i);
					JSONSerializable serializable = (JSONSerializable)type.getClass()
							.getDeclaredConstructor(JSONObject.class)
							.newInstance(json);
					set.add(serializable);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {}
			}else set.add(array.get(i));
		}
		return set;
	}
	
	private JSONArray serializeList(List<Object> set){
		JSONArray array = new JSONArray();
		if(set==null) return array;
		Iterator<Object> i = set.iterator();
		while(i.hasNext()){
			Object obj = i.next();
			if(obj != null){
				if(JSONSerializable.class.isAssignableFrom(obj.getClass())){
					JSONSerializable serializable = (JSONSerializable)obj;
					array.put(serializable.serializeToJSONObject());					
				}else array.put(obj);
			}
		}
		return array;
	}
	
	private List<Object> deserializeList(JSONArray array, Type type) throws JSONException{
		List<Object> set = new ArrayList<Object>();
		for(int i = 0; i < array.length(); i++){
			if(JSONSerializable.class.isAssignableFrom(type.getClass())){
				try {
					JSONObject json = array.getJSONObject(i);
					JSONSerializable serializable = (JSONSerializable)type.getClass()
							.getDeclaredConstructor(JSONObject.class)
							.newInstance(json);
					set.add(serializable);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {}
			}else set.add(array.get(i));
		}
		return set;
	}
	
	private static boolean shouldBeSerialized(Field field){
		if(field.isAnnotationPresent(Serialize.class))
			return true;
		return false;
	}
	
	private static boolean shouldBeDeserialized(Field field){
		if(field.isAnnotationPresent(Deserialize.class))
			return true;
		return false;
	}
	
	@Documented
	@Target(ElementType.FIELD)
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Serialize{}
	
	@Documented
	@Target(ElementType.FIELD)
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Deserialize{
		boolean optional() default false;
	}
	
	
}
