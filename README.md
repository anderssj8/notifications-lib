# Notifications Library

Librería Java 21, independiente de frameworks, que unifica correo, SMS y push. Las integraciones incluidas simulan las respuestas mínimas de SendGrid, Twilio y Firebase Cloud Messaging; no realizan HTTP ni exponen credenciales.

## Diseño

- `NotificationClient`: API síncrona, asíncrona y por lotes.
- `NotificationProvider`: estrategia reemplazable por canal (Strategy/Adapter).
- `NotificationLibrary.Builder`: configuración y composición en Java puro (Builder/Factory).
- `NotificationValidator`: validación intercambiable.
- `RetryPolicy`: reintento exponencial únicamente para errores marcados como temporales.
- `EventPublisher`: Pub/Sub de estados `QUEUED`, `SENT`, `DELIVERED` y `FAILED`.
- `TemplateEngine`: plantillas desacopladas; se incluye una implementación en memoria.

Agregar o cambiar un proveedor no requiere modificar el cliente: basta implementar `NotificationProvider` y registrarlo para su canal. Un canal conserva campos opcionales: `subject` se utiliza en correo y se ignora en SMS/push.

## Instalación

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>notifications-lib</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'com.example:notifications-lib:1.0.0'
```

Para instalarla primero en el repositorio Maven local:

```bash
./mvnw clean install
```

## Inicio rápido y configuración

```java
var events = new InMemoryEventPublisher();
events.subscribe(event -> System.out.println(event.status()));

try (var client = NotificationLibrary.builder()
    .provider(new SendGridEmailProvider(
        new SendGridConfig(System.getenv("SENDGRID_API_KEY").toCharArray(),
                           "noreply@example.com")))
    .provider(new TwilioSmsProvider(
        new TwilioConfig("AC...", System.getenv("TWILIO_TOKEN").toCharArray(),
                         "+15005550006")))
    .provider(new FirebasePushProvider(
        new FirebaseConfig("my-project", System.getenv("FCM_TOKEN").toCharArray())))
    .templates(new InMemoryTemplateEngine(Map.of(
        "welcome", "Hola {{name}}, bienvenida/o.")))
    .retryPolicy(RetryPolicy.exponential(3, Duration.ofMillis(100)))
    .eventPublisher(events)
    .build()) {

  var notification = Notification.builder()
      .channel(Channel.EMAIL)
      .recipient("user@example.com")
      .subject("Bienvenida")
      .template("welcome", Map.of("name", "José"))
      .build();

  SendResult result = client.sendAsync(notification).join();
}
```

`sendBatchAsync(List<Notification>)` ejecuta los envíos en paralelo mediante `CompletableFuture`. Los errores asíncronos se reciben como `CompletionException`; su causa será `ValidationException` o `DeliveryException`.

## Proveedores compatibles

| Canal | Proveedor simulado | Respuesta representativa |
|---|---|---|
| Correo | SendGrid Mail Send v3 | aceptación/ID `sg-*` |
| SMS | Twilio Programmable Messaging | SID `SM*`, estado en cola |
| Push | Firebase Cloud Messaging HTTP v1 | nombre `projects/.../messages/...` |

Los códigos modelados incluyen HTTP 400/401/429/500 de SendGrid; 21211, 21610, 20429 y fallos 5xx de Twilio; e `INVALID_ARGUMENT`, `UNREGISTERED`, `QUOTA_EXCEEDED` y `UNAVAILABLE` de FCM. `ErrorCode.retryable()` evita reintentar errores permanentes.

## Referencia de API

| Tipo | Responsabilidad |
|---|---|
| `Notification` | Solicitud inmutable creada con Builder |
| `NotificationClient` | `send`, `sendAsync`, `sendBatchAsync` |
| `SendResult` | proveedor, ID externo, estado, intentos y fecha |
| `NotificationProvider` | punto de extensión para proveedores |
| `NotificationValidator` | punto de extensión para reglas |
| `DeliveryEventListener` | suscriptor de cambios de estado |
| `RetryPolicy` | intentos, demora inicial y multiplicador |

## Extender la librería

```java
public final class MailgunProvider implements NotificationProvider {
  public Channel channel() { return Channel.EMAIL; }
  public String name() { return "Mailgun"; }
  public SendResult send(Notification value) { /* adaptar y simular/llamar API */ }
}

var client = NotificationLibrary.builder()
    .provider(new MailgunProvider())
    .build();
```

Para añadir un canal completamente nuevo, agréguelo a `Channel`, implemente su validador y registre una estrategia. La lógica de orquestación, lotes, eventos y reintentos permanece sin cambios.

## Errores

```java
try {
  client.send(notification);
} catch (ValidationException e) {
  System.err.println("Entrada: " + e.code());
} catch (DeliveryException e) {
  System.err.println("Proveedor: " + e.code());
}
```

La validación exige email sintácticamente válido, teléfono en formato E.164 y token push básico. En producción deben añadirse límites de tamaño y reglas específicas del proveedor.

## Seguridad

- Obtenga secretos desde un secret manager o variables de entorno en la aplicación consumidora; nunca los confirme al repositorio.
- Los secretos se reciben como `char[]`, se copian defensivamente y nunca se imprimen. Borre el arreglo original tras construir la configuración si su modelo de amenazas lo exige.
- Rote claves, aplique mínimo privilegio y separe credenciales por ambiente.
- Los destinatarios se enmascaran en logs. No registre cuerpos si pueden contener datos personales.
- Esta demo no hace conexiones reales. Una integración real debe usar TLS, timeouts, rate limiting y SDKs/versiones fijadas.

## Pruebas y demo

```bash
./mvnw test
./mvnw package
java -cp 'target/notifications-lib-1.0.0.jar:target/dependency/*' com.example.notifications.examples.NotificationExamples
```

Las pruebas usan Mockito y verifican selección del proveedor, eventos, validación asíncrona y reintentos.

Con Docker:

```bash
docker build -t notifications-lib .
docker run --rm notifications-lib
```

## Documentación consultada

- SendGrid Mail Send v3: https://www.twilio.com/docs/sendgrid/api-reference/mail-send/mail-send
- Twilio Message Resource: https://www.twilio.com/docs/messaging/api/message-resource
- Twilio REST API responses/errors: https://www.twilio.com/docs/usage/twilios-response
- FCM HTTP v1 send: https://firebase.google.com/docs/cloud-messaging/send-message
- FCM error codes: https://firebase.google.com/docs/cloud-messaging/error-codes
