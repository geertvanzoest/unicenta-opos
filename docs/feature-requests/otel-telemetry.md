# [Feature]: OpenTelemetry (OTel) / Telemetry implementeren

**Labels:** enhancement

## Probleem of aanleiding

Er is momenteel geen observability/telemetry ingebouwd in uniCenta oPOS. Dit maakt het lastig om:

- Prestatieproblemen te diagnosticeren in productie (trage queries, langzame UI-responses)
- Fouten en exceptions gestructureerd te monitoren
- Inzicht te krijgen in gebruikspatronen (welke modules worden het meest gebruikt, piekuren)
- Problemen met database-connecties of externe integraties vroegtijdig te signaleren

Voor een POS-systeem dat in een live omgeving draait (zoals bij HSC Jeka) is goede observability essentieel om downtime te minimaliseren.

## Gewenste oplossing

Implementeer [OpenTelemetry](https://opentelemetry.io/) (OTel) als telemetry-framework. Dit omvat:

### Traces
- Instrumentatie van belangrijke operaties (verkoop-transacties, database-queries, rapportage-generatie)
- Distributed tracing voor database-calls en eventuele externe API-calls
- Span-attributen met relevante context (gebruiker, module, transactie-type)

### Metrics
- JVM-metrics (heap, GC, threads)
- Applicatie-metrics (transacties per uur, gemiddelde transactietijd, aantal actieve sessies)
- Database connection pool metrics
- Custom business metrics (omzet per tijdseenheid, foutpercentage)

### Logs
- Gestructureerde logging via SLF4J/Logback met OTel-integratie
- Correlatie tussen logs en traces (trace-id in logregels)

### Exporters
- Ondersteuning voor OTLP (OpenTelemetry Protocol) als standaard export-formaat
- Configureerbaar via `opentelemetry.properties` of environment variables
- Compatibel met backends zoals Jaeger, Grafana Tempo, Prometheus, etc.

### Configuratie
- OTel standaard uitgeschakeld (opt-in) om bestaande installaties niet te belasten
- Configuratie via properties-bestand of environment variables
- Mogelijkheid om individuele instrumentatie aan/uit te zetten

## Alternatieven overwogen

- **Micrometer + Prometheus**: Goed voor metrics, maar mist tracing-ondersteuning
- **Handmatige logging**: Huidige aanpak, maar ongestructureerd en moeilijk te correleren
- **JavaFX Performance Monitor**: Alleen UI-gerelateerd, geen backend-observability
- **OpenTelemetry** is de industriestandaard en biedt een uniform framework voor traces, metrics en logs

## Module

Overig (cross-cutting concern, raakt alle modules)

## Technische details

### Dependencies (Maven)

```xml
<!-- OpenTelemetry BOM -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-bom</artifactId>
    <version>1.40.0</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- API + SDK -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
</dependency>

<!-- OTLP Exporter -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>

<!-- Auto-configuratie -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk-extension-autoconfigure</artifactId>
</dependency>
```

### Aanpak (gefaseerd)

1. **Fase 1**: OTel SDK toevoegen, basis-configuratie, JVM-metrics
2. **Fase 2**: Instrumentatie van database-laag (`com.unicenta.data`)
3. **Fase 3**: Instrumentatie van POS-transacties (`com.unicenta.pos.sales`)
4. **Fase 4**: Gestructureerde logging met trace-correlatie
5. **Fase 5**: Dashboard-templates (Grafana) voor standaard monitoring

## Extra context

- Java 11 compatibiliteit is vereist (OTel SDK ondersteunt Java 8+)
- Minimale impact op bestaande codebase (instrumentatie via wrapper/interceptor pattern)
- Geen runtime overhead wanneer telemetry uitgeschakeld is
