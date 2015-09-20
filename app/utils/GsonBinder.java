package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import play.data.binding.Global;
import play.data.binding.TypeBinder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Global
public class GsonBinder implements TypeBinder<JsonElement>
{
  public Object bind(String name, Annotation[] notes, String value, Class toClass, Type toType) throws Exception
  {
    return new JsonParser().parse(value);
  }
}