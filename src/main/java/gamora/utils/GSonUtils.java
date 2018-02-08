package gamora.utils;

public class GSonUtils {

  private final String CONST_DATA_PATTERN = "dd/MM/yyyy HH:mm:ss";

  private static GSonUtils instance = new GSonUtils();
  public static GSonUtils getInstance() { return instance; }

  private com.google.gson.Gson gson;

  private GSonUtils() {
    this.gson = new com.google.gson.GsonBuilder()
                  .disableHtmlEscaping()
                  .setDateFormat(CONST_DATA_PATTERN)
                  .setPrettyPrinting()
                  .serializeNulls()
                  .setExclusionStrategies(new gamora.orm.json.EstrategiaIgnorarCampoPorAnnotation())
               .create();
  }

  public String obj2Json(Object obj) {
    return new String(this.gson.toJson(obj));
  }

}
