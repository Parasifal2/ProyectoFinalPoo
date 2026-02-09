/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VisitaTecnica {
    private final String id;
    private LocalDateTime fechaProgramada;
    private EstadoVisita estado;

    // MUCHOS técnicos por visita
    private final List<Tecnico> tecnicosAsignados = new ArrayList<>();

    private String resultado;
    private String motivoReagendamiento;
    private String motivoCancelacion;
    private double ingresoGenerado;

    public VisitaTecnica(LocalDateTime fechaProgramada) {
        this.id = UUID.randomUUID().toString();
        this.fechaProgramada = fechaProgramada;
        this.estado = EstadoVisita.PROGRAMADA;
    }

    public String getId() { return id; }
    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public EstadoVisita getEstado() { return estado; }
    public void setEstado(EstadoVisita estado) { this.estado = estado; }

    public List<Tecnico> getTecnicosAsignados() { return tecnicosAsignados; }

    public void agregarTecnico(Tecnico t) {
        if (t == null) return;
        if (!tecnicosAsignados.contains(t)) tecnicosAsignados.add(t);
    }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getMotivoReagendamiento() { return motivoReagendamiento; }
    public void setMotivoReagendamiento(String motivoReagendamiento) { this.motivoReagendamiento = motivoReagendamiento; }

    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }

    public double getIngresoGenerado() { return ingresoGenerado; }
    public void setIngresoGenerado(double ingresoGenerado) { this.ingresoGenerado = ingresoGenerado; }

    @Override
    public String toString() {
        return "Visita{id=" + id + ", fecha=" + fechaProgramada + ", estado=" + estado +
                ", tecnicos=" + tecnicosAsignados.size() + "}";
    }
}
