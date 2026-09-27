package com.example.notifications.validation;
import com.example.notifications.api.*; import com.example.notifications.error.*; import java.util.regex.Pattern;
public final class DefaultNotificationValidator implements NotificationValidator {
  private static final Pattern EMAIL=Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",Pattern.CASE_INSENSITIVE);
  private static final Pattern PHONE=Pattern.compile("^\\+[1-9]\\d{7,14}$"); private static final Pattern TOKEN=Pattern.compile("^[A-Za-z0-9_:\\-]{20,4096}$");
  public void validate(Notification n){
    if(n==null||n.channel()==null||blank(n.recipient())||(blank(n.body())&&blank(n.templateId()))) throw new ValidationException(ErrorCode.INVALID_NOTIFICATION,"Canal, destinatario y cuerpo o plantilla son obligatorios");
    switch(n.channel()) { case EMAIL -> {if(!EMAIL.matcher(n.recipient()).matches()) fail(ErrorCode.INVALID_EMAIL,"Correo inválido");}
      case SMS -> {if(!PHONE.matcher(n.recipient()).matches()) fail(ErrorCode.INVALID_PHONE,"Teléfono inválido; use E.164");}
      case PUSH -> {if(!TOKEN.matcher(n.recipient()).matches()) fail(ErrorCode.INVALID_DEVICE_TOKEN,"Token de dispositivo inválido");} }
  }
  private static boolean blank(String s){return s==null||s.isBlank();} private static void fail(ErrorCode c,String m){throw new ValidationException(c,m);}
}
