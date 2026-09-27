package com.example.notifications.template;
import java.util.Map;
public interface TemplateEngine { String render(String id, Map<String,String> variables); }
