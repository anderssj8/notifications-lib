package com.example.notifications.template;
import com.example.notifications.error.*; import java.util.*;
public final class InMemoryTemplateEngine implements TemplateEngine {
  private final Map<String,String> templates; public InMemoryTemplateEngine(Map<String,String> templates){this.templates=Map.copyOf(templates);}
  public String render(String id,Map<String,String> vars){String value=templates.get(id);if(value==null)throw new ValidationException(ErrorCode.TEMPLATE_NOT_FOUND,"Plantilla no encontrada: "+id); for(var e:vars.entrySet())value=value.replace("{{"+e.getKey()+"}}",e.getValue());return value;}
}
