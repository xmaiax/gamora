package br.fmaia.gamora.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.fmaia.gamora.orm.json.EstrategiaIgnorarCampoPorAnnotation;

public class GSonUtils {

  private final String CONST_DATA_PATTERN = "dd/MM/yyyy HH:mm:ss";
  
  private static GSonUtils instance = new GSonUtils();
  public static GSonUtils getInstance() { return instance; }
  
  private Gson gson;
  
  private GSonUtils() {
    this.gson = new GsonBuilder()
                  .disableHtmlEscaping()
                  .setDateFormat(CONST_DATA_PATTERN)
                  .setPrettyPrinting()
                  .serializeNulls()
                  .setExclusionStrategies(new EstrategiaIgnorarCampoPorAnnotation())
               .create();
  }
  
  public String obj2Json(Object obj) {
    return new String(this.gson.toJson(obj));
  }
  
}
