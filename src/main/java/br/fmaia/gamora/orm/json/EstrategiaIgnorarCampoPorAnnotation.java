package br.fmaia.gamora.orm.json;

public class EstrategiaIgnorarCampoPorAnnotation implements com.google.gson.ExclusionStrategy {
  @Override public boolean shouldSkipClass(Class<?> nada) { return false; }
  @Override public boolean shouldSkipField(com.google.gson.FieldAttributes fieldAttributes) { return fieldAttributes.getAnnotation(IgnorarJSON.class) != null; }
}
