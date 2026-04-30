//ai-service/src/main/java/dev/sammy_ulfh/ai/dto/AiAnalysisResponse.java
package dev.sammy_ulfh.ai.dto;

import java.util.List;

public class AiAnalysisResponse {

    private List<String> problemas;
    private List<String> riesgos;
    private List<String> recomendaciones;
    private String cargaGeneral;

    // 🔥 Constructor completo
    public AiAnalysisResponse(List<String> problemas, List<String> riesgos,
                              List<String> recomendaciones, String cargaGeneral) {
        this.problemas = problemas;
        this.riesgos = riesgos;
        this.recomendaciones = recomendaciones;
        this.cargaGeneral = cargaGeneral;
    }

    // 🔥 Constructor vacío (necesario para Jackson)
    public AiAnalysisResponse() {}

    // Getters y setters

    public List<String> getProblemas() {
        return problemas;
    }

    public void setProblemas(List<String> problemas) {
        this.problemas = problemas;
    }

    public List<String> getRiesgos() {
        return riesgos;
    }

    public void setRiesgos(List<String> riesgos) {
        this.riesgos = riesgos;
    }

    public List<String> getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(List<String> recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    public String getCargaGeneral() {
        return cargaGeneral;
    }

    public void setCargaGeneral(String cargaGeneral) {
        this.cargaGeneral = cargaGeneral;
    }
}